package kr.co.tj.sellservice.info.dto;

import kr.co.tj.sellservice.SellState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellInfoRequest {
	
	private String id;
	
	private String username;
	private String buyer;
	private String productName;
	private Long price;
	private Double longitude;
	private Double latitude;
	private SellState sellState;
	
	private boolean isReviewed;
	
}
