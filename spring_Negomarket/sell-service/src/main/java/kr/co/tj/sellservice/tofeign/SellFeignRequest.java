package kr.co.tj.sellservice.tofeign;


import kr.co.tj.sellservice.img.dto.SellImgRequest;
import kr.co.tj.sellservice.info.dto.SellInfoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SellFeignRequest {
	
	private SellImgRequest sellImgRequest;
	private SellInfoRequest sellInfoRequest;

}
