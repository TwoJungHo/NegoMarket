package kr.co.tj.chatservice.message.service;

import java.util.List;

import kr.co.tj.chatservice.message.dto.ChatMessageDTO;

public interface ChatMessageService {

	void saveAndSendMessage(ChatMessageDTO dto);
	
	void readMessage(Long id);

	List<ChatMessageDTO> getMessageList(String roomTitle);

}
