package kr.co.tj.boardservice.dto;

import java.io.Serializable;
import java.util.Date;

import kr.co.tj.boardservice.persistence.BoardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String sellId;
	
	private String username;

	private String title;
	
	private String deltaString;

	private String htmlString;
	
	

}
