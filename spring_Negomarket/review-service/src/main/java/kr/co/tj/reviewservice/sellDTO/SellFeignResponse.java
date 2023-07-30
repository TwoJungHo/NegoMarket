package kr.co.tj.reviewservice.sellDTO;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SellFeignResponse {
	
	
	private SellInfoResponse sellInfoResponse;
	private Long imgPathId;

}
