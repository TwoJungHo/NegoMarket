package kr.co.tj.reviewservice.sellDTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellInfoDTO {

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

	boolean isReviewed;

}
