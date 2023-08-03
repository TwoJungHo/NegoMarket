package kr.co.tj.chatservice.room.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import kr.co.tj.chatservice.DataNotFoundException;
import kr.co.tj.chatservice.message.dto.ChatMessageResponse;
import kr.co.tj.chatservice.message.persistence.ChatMessageEntity;
import kr.co.tj.chatservice.message.persistence.ChatMessageRepository;
import kr.co.tj.chatservice.room.dto.ChatRoomDTO;
import kr.co.tj.chatservice.room.dto.ChatRoomResponse;
import kr.co.tj.chatservice.room.persistence.ChatRoomEntity;
import kr.co.tj.chatservice.room.persistence.ChatRoomRepository;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

	private ChatRoomRepository chatRoomRepository;
	private ChatMessageRepository chatMessageRepository;
	private SimpMessageSendingOperations messagingTemplate;

	@Autowired
	public ChatRoomServiceImpl(
			ChatRoomRepository chatRoomRepository,
			ChatMessageRepository chatMessageRepository,
			SimpMessageSendingOperations messagingTemplate) {
		super();
		this.chatRoomRepository = chatRoomRepository;
		this.chatMessageRepository = chatMessageRepository;
		this.messagingTemplate = messagingTemplate;
	}

	@Override
	public ChatRoomDTO insertRoom(String username1, String username2, ChatRoomDTO dto) {

		ChatRoomEntity entity = ChatRoomEntity.builder().title(dto.getTitle()).username1(dto.getUsername1())
				.username2(dto.getUsername2()).build();

		entity = chatRoomRepository.save(entity);
		
		ChatRoomResponse chatRoomResponse= ChatRoomResponse.builder()
		.requestSubject("")
		.title(entity.getTitle())
		.username1(entity.getUsername1())
		.username2(entity.getUsername2())
		.build();
		
		messagingTemplate.convertAndSend("/sub/chatroomnotify/" + username1, chatRoomResponse);
		messagingTemplate.convertAndSend("/sub/chatroomnotify/" + username2, chatRoomResponse);

		dto.setId(entity.getId());

		return dto;

	}

	// 사용자 2명의 username을 이용하여 만든 방제로 검색하여, 해당 대화방이 존재할 경우
	// 대화내용을 포함한 dto객체를 반환하고
	// 존재하지 않을 경우, null을 반환함.
	@Override
	public Map<String, Object> enter(String title) {
		Optional<ChatRoomEntity> optional = chatRoomRepository.findByTitle(title);

		if (!optional.isPresent()) {

			return null;
		}

		/*
		 * 방이 존재할 경우, 방 정보 뿐만 아니라 해당 방에 대해서 작성된 모든 message들을 List로 같이 반환해줄 것임. 따라서 2가지
		 * 객체(챗방 dto와 message List)를 한번에 보내야 하는 상황 Map을 이용
		 */

		ChatRoomEntity entity = optional.get();
		ChatRoomDTO dto = ChatRoomDTO.builder().id(entity.getId()).title(entity.getTitle())
				.username1(entity.getUsername1()).username2(entity.getUsername2()).build();

		List<ChatMessageEntity> list = chatMessageRepository.findByRoomTitle(entity.getTitle());
		List<ChatMessageResponse> messageList = new ArrayList<>();
		for (ChatMessageEntity x : list) {
			ChatMessageResponse response = ChatMessageResponse.builder().roomTitle(x.getRoomTitle())
					.sendAt(x.getSendAt()).sender(x.getSender()).receiver(x.getReceiver()).message(x.getMessage())
					.build();
			messageList.add(response);
		}

		// 자바 컬렉션 프레임웍 자료형은 List를 정렬하기 위해
		// Comparator 인터페이스를 구현하는 객체 생성. 메시지 발송 시각으로 비교.
		Comparator<ChatMessageResponse> comparator = new Comparator<ChatMessageResponse>() {
			@Override
			public int compare(ChatMessageResponse res1, ChatMessageResponse res2) {
				return res1.getSendAt().compareTo(res2.getSendAt());
			}
		};

		// 발송 시각 기준으로 정렬
		Collections.sort(messageList, comparator);

		Map<String, Object> map = new HashMap<>();
		map.put("roomInfo", dto);
		map.put("messageList", messageList);

		return map;
	}
	

	@Transactional
	@Override
	public String delete(String title) {
		Optional<ChatRoomEntity> optional = chatRoomRepository.findByTitle(title);

		if (!optional.isPresent()) {
			throw new DataNotFoundException("삭제할 채팅방이 존재하지 않습니다.");
		}
		
		ChatRoomEntity entity = optional.get();
		chatRoomRepository.delete(entity);
		
		List<ChatMessageEntity> messageList = chatMessageRepository.findByRoomTitle(entity.getTitle());
		
		for(ChatMessageEntity x : messageList) {
			chatMessageRepository.delete(x);
		}
		return "삭제 완료";
	}

	@Override
	public List<ChatRoomDTO> findRoomsByUsername(String username) {
		
		List<ChatRoomDTO> chatRoomDTOList = new ArrayList<>();
		List<ChatRoomEntity> chatRoomEntityList = chatRoomRepository.findByUsername1ContainingOrUsername2Containing(username, username);
		for(ChatRoomEntity x : chatRoomEntityList) {
			
			ChatRoomDTO chatRoomDTO = ChatRoomDTO.builder()
					.id(x.getId())
					.title(x.getTitle())
					.username1(x.getUsername1())
					.username2(x.getUsername2())
					.build();
			chatRoomDTOList.add(chatRoomDTO);
		}
		return chatRoomDTOList;
	}

}