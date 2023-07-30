package kr.co.tj.userservice.info.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoRequest{
	
	private String username;
	
	private String password;
	private String password2;
	private String orgPassword;
	
	private Double longitude;
	private Double latitude;
	
	private String name;
	
}
