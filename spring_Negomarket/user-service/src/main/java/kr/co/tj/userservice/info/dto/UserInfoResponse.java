package kr.co.tj.userservice.info.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse implements Serializable {
	


	private static final long serialVersionUID = 1L;

	private String username;
	private String name;

	private Double longitude;
	private Double latitude;
	
	private Date createAt;
	private Date updateAt;
	
	private String token;
	
		

}
