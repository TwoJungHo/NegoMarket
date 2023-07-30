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
public class RateStatsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private float averageRate;
	private float minRate;
	private float maxRate;

}
