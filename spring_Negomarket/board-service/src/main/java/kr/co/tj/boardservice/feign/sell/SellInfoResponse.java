package kr.co.tj.boardservice.feign.sell;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellInfoResponse {
	
	private String id;
	
	private String username;
	private String buyer;
	private String productName;
	private Long price;
	
	private Date createAt;
	private Date updateAt;
	private Date finishAt;
	
	private Double longitude;
	private Double latitude;
	private SellState sellState;
	private boolean isReviewed;
	
}
