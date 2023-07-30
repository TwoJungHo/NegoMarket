package kr.co.tj.statistic.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String sellerId;
	
	private Long rid;

	private float rate;
	
	private int count;

	public UpdateReviewRateEntity toReviewEntity() {
		return UpdateReviewRateEntity.builder()
				.sellerId(sellerId)
				.rid(rid)
				.rate(rate)
				.count(count)
				.build();
	}

}
