package kr.co.tj.statistic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AverageRateResponse {
   private String sellerId;

   private float rate;
   
   private float averageRate;
}