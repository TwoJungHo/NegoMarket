package kr.co.tj.reviewservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import kr.co.tj.reviewservice.sellDTO.SellFeignResponse;
import kr.co.tj.reviewservice.sellDTO.SellInfoRequest;

@FeignClient(name = "sell-service")
public interface SellFeign {

	@PutMapping("sell-service/review-sells/isreviewed")
	   public ResponseEntity<SellFeignResponse> updateIsReviewed(@RequestBody SellInfoRequest sellInfoRequest);
	
}
