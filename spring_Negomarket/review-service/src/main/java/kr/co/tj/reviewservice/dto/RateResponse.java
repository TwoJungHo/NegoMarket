package kr.co.tj.reviewservice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String sellerId;

	private float rate;

	private Long reviewId;
}
