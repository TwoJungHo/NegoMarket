package kr.co.tj.userservice.pic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class UserPicRequest {
	
	
	private String username;
	
	private String filename;
	

}
