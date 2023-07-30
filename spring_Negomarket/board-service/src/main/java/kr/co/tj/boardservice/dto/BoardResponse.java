package kr.co.tj.boardservice.dto;

import java.io.Serializable;



import kr.co.tj.boardservice.feign.sell.SellInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String sellId;
	
	private String username;

	private String title;
	
	private String deltaString;

	private String htmlString;
	
	private SellInfoResponse sellInfoResponse;
	
	
	
}
