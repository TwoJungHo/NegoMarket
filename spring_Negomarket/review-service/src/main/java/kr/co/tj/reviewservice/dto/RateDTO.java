package kr.co.tj.reviewservice.dto;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class RateDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String sellerId;
	
	private Long rid;

	private float rate;
	
//	public RateDTO toDTO(ReviewDTO dto) {
//		return RateDTO.builder()
//				.sellerId(dto.getSellerId())
//				.rid(dto.getId())
//				.count(count+1)
//				.build();
//	}

//	public UpdateReviewRateEntity toReviewEntity() {
//		return UpdateReviewRateEntity.builder()
//				.sellerId(sellerId)
//				.rid(rid)
//				.rate(rate)
//				.count(count)
//				.build();
//	}

}
