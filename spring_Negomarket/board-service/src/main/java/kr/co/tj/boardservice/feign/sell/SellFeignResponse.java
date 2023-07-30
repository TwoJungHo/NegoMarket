package kr.co.tj.boardservice.feign.sell;



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
	private String imgUploadResult;

}
