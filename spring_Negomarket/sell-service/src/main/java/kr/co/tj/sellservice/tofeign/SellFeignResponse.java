package kr.co.tj.sellservice.tofeign;



import kr.co.tj.sellservice.info.dto.SellInfoResponse;
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
