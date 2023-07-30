package kr.co.tj.chatservice.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {
	private String requestSubject;
	private String title;
	private String username1;
	private String username2;

}
