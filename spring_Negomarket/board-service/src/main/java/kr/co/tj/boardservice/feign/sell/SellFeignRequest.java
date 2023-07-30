package kr.co.tj.boardservice.feign.sell;


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
