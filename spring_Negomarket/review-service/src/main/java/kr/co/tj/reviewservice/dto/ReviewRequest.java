package kr.co.tj.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest{
	
	private Long id;
	
	private String sellId; // sellInfo의 id값
	
	private String sellerName; // 판매자 username

	private String buyerName; // 구매자 username

	private String content;

	private float rate;

}
