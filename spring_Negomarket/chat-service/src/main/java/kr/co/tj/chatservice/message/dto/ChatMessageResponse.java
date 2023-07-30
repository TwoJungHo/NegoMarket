package kr.co.tj.chatservice.message.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageResponse {
	
	private long id;
	private String roomTitle;
	private Date sendAt;
	private String sender;
	private String receiver;
	private String message;
	private boolean isRead;
	
}
