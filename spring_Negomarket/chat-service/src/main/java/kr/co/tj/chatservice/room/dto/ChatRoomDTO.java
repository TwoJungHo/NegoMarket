package kr.co.tj.chatservice.room.dto;









import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {

	private Long id;
	private String title;
	private String username1;
	private String username2;
	


}