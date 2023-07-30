package kr.co.tj.chatservice.message.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import kr.co.tj.chatservice.DataNotFoundException;
import kr.co.tj.chatservice.message.dto.ChatMessageDTO;
import kr.co.tj.chatservice.message.dto.ChatMessageReadStateResponse;
import kr.co.tj.chatservice.message.dto.ChatMessageResponse;
import kr.co.tj.chatservice.message.persistence.ChatMessageEntity;
import kr.co.tj.chatservice.message.persistence.ChatMessageRepository;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
	
	private SimpMessageSendingOperations messagingTemplate;
	private ChatMessageRepository chatMessageRepository;
	
	@Autowired
	public ChatMessageServiceImpl(
			SimpMessageSendingOperations messagingTemplate,
			ChatMessageRepository chatMessageRepository) {
		
		super();
		this.messagingTemplate = messagingTemplate;
		this.chatMessageRepository = chatMessageRepository;
	}


	@Override
	@Transactional
	public List<ChatMessageDTO> getMessageList(String roomTitle) {
		
		List<ChatMessageEntity> entityList = chatMessageRepository.findByRoomTitle(roomTitle);
		List<ChatMessageDTO> dtoList = new ArrayList<>();
		for (ChatMessageEntity x : entityList) {
			ChatMessageDTO dto = ChatMessageDTO.builder()
					.id(x.getId())
					.roomTitle(x.getRoomTitle())
					.sendAt(x.getSendAt())
					.sender(x.getSender())
					.receiver(x.getReceiver())
					.message(x.getMessage())
					.isRead(x.isRead())
					.build();
			dtoList.add(dto);
		}
		
		return dtoList;
		
	}
	
	
	@Override
	@Transactional
	public void readMessage(Long id) {
		
		Optional<ChatMessageEntity> optional = chatMessageRepository.findById(id);
		
		if(!optional.isPresent()) {
			throw new DataNotFoundException("잘못된 요청");
		}
		
		ChatMessageEntity chatMessageEntity = optional.get();
		chatMessageEntity.setRead(true);
		chatMessageEntity = chatMessageRepository.save(chatMessageEntity);
		
		ChatMessageReadStateResponse readResponse = ChatMessageReadStateResponse.builder()
				.id(chatMessageEntity.getId())
				.isRead(chatMessageEntity.isRead())
				.build();
		
	messagingTemplate.convertAndSend("/sub/chatroom/read/" + chatMessageEntity.getRoomTitle(), readResponse);
		
	}
	
	
	@Override
	@Transactional
	public void saveAndSendMessage(ChatMessageDTO dto) {
		
		Date date = new Date();
		
		ChatMessageEntity entity = ChatMessageEntity.builder()
				.roomTitle(dto.getRoomTitle())
				.sendAt(date)
				.sender(dto.getSender())
				.receiver(dto.getReceiver())
				.message(dto.getMessage())
				.isRead(false)
				.build();
		
		entity = chatMessageRepository.save(entity);
		
		ChatMessageResponse response = ChatMessageResponse.builder()
				.id(entity.getId())
				.roomTitle(entity.getRoomTitle())
				.sendAt(entity.getSendAt())
				.sender(entity.getSender())
				.receiver(entity.getReceiver())
				.message(entity.getMessage())
				.isRead(false)
				.build();
	
	messagingTemplate.convertAndSend("/sub/chatroom/" + response.getRoomTitle(), response);
	
		
	}
	
}
