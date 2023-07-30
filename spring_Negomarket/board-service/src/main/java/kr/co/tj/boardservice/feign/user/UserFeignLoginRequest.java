package kr.co.tj.boardservice.feign.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFeignLoginRequest{
	
	private String username;
	
	private String password;
	
}
