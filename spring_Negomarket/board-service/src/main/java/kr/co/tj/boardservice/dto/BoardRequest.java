package kr.co.tj.boardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardRequest {
	
	private String sellId;
	
	private String username;
	
	private String title;
	
	private String deltaString;
	
	private String htmlString;

}
