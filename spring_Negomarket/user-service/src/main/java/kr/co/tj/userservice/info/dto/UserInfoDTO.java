package kr.co.tj.userservice.info.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import kr.co.tj.userservice.info.persistence.UserInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String username;
	private String password;
	private String name;

	private Double longitude;
	private Double latitude;

	private Date createAt;
	private Date updateAt;
	
	private String token;
	
	
	public static UserInfoDTO toUserDTO(UserInfoRequest ureq) {
		return UserInfoDTO.builder()
				.username(ureq.getUsername())
				.password(ureq.getPassword())
				.name(ureq.getName())
				.build();
	}
	
	
	public UserInfoResponse toUserResponse() {
		return UserInfoResponse.builder()
				.username(username)
				.name(name)
				.createAt(createAt)
				.updateAt(updateAt)
				.token(token)
				.build();
	}
	
	
	public UserInfoEntity toUserEntity() {
		return UserInfoEntity.builder()
				.username(username)
				.password(password)
				.name(name)
				.createAt(createAt)
				.updateAt(updateAt)
				.build();
	}

	
	
	
	public UserInfoDTO toUserDTO(UserInfoEntity userInfoEntity) {
		this.username = userInfoEntity.getUsername();
		this.name = userInfoEntity.getName();
		this.createAt = userInfoEntity.getCreateAt();
		this.updateAt = userInfoEntity.getUpdateAt();

		return this;
	}


	

	
	
	public static UserInfoDTO toUserDTO(UserLoginRequest userLoginRequest) {
		
		return UserInfoDTO.builder()
				.username(userLoginRequest.getUsername())
				.password(userLoginRequest.getPassword())
				.build();
	}

}
