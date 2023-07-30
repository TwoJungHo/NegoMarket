package kr.co.tj.reviewservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import kr.co.tj.reviewservice.dto.RateDTO;



@FeignClient(name = "updateReviewRate-service")
public interface ReviewRateFeign {
	
	
	// 별점 입력
	@GetMapping("updateReviewRate-service/rate/{sellerId}")
	public String leaveReview(@PathVariable("sellerId") String sellerId, @RequestBody RateDTO rateDTO);
	
	// 별점 수정
	@PutMapping("updateReviewRate-service/rate/update/{rid}")
	public String updateRate(@PathVariable("rid") Long rid, RateDTO rateDTO);
	
	// 별점 삭제
	@DeleteMapping("updateReviewRate-service/rate/{rid}")
	public String deleteRate(@PathVariable("rid") Long rid, @RequestBody RateDTO rateDTO);
	
	
}
