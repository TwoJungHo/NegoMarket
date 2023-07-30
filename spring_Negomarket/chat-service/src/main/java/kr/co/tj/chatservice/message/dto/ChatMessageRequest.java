package kr.co.tj.chatservice.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageRequest {
	
	private String roomTitle;
	private String receiver;
	private String message;
	
}
