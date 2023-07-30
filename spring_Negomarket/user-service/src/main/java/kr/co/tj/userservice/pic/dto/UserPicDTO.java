package kr.co.tj.userservice.pic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class UserPicDTO {
	
	private Long id;
	
	
	private String username;
	
	private String filename;
	
	
	private byte[] picData;

}
