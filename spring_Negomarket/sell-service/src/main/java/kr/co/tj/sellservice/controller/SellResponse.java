package kr.co.tj.sellservice.controller;



import kr.co.tj.sellservice.info.dto.SellInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SellResponse {
	
	
	private SellInfoResponse sellInfoResponse;
	private Long imgPathId;

}
