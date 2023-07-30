package kr.co.tj.statistic.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.tj.statistic.dto.RateDTO;
import kr.co.tj.statistic.service.UpdateReviewRateServiceImpl;

@RestController
@RequestMapping("updateReviewRate-service")
public class StatisticController {

	@Autowired
	private UpdateReviewRateServiceImpl updateReviewRateService;

	@Autowired
	private Environment env;

	// 별점 입력
	@PostMapping("/rate/{sellerId}")
	public ResponseEntity<?> leaveReview(@PathVariable("sellerId") String sellerId, @RequestBody RateDTO rateDTO) {
		try {
			updateReviewRateService.leaveRate(sellerId, rateDTO);
			return ResponseEntity.status(HttpStatus.OK).body("별점이 업데이트되었습니다.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("별점 업데이트에 실패했습니다.");
		}
	}
	
	// 별점 수정
	@PutMapping("/rate/{rid}")
	public ResponseEntity<?> updateRate(@PathVariable("rid") Long rid, @RequestBody RateDTO rateDTO) {
	    try {
	        updateReviewRateService.updateRate(rid, rateDTO);
	        return ResponseEntity.status(HttpStatus.OK).body("별점이 성공적으로 수정되었습니다.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("별점 수정에 실패했습니다: " + e.getMessage());
	    }
	}

	// 별점 삭제
	@DeleteMapping("/rate/{rid}")
	public ResponseEntity<?> deleteRate(@PathVariable("rid") Long rid, @RequestBody RateDTO rateDTO) {
	    try {
	        updateReviewRateService.deleteRate(rid, rateDTO);
	        return ResponseEntity.status(HttpStatus.OK).body("별점이 성공적으로 삭제되었습니다.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("별점 삭제에 실패했습니다: " + e.getMessage());
	    }
	}


	// 별점 평균 & 총 별점 입력 횟수
	@GetMapping("/rate/{sellerId}/stats")
	public ResponseEntity<?> getAverageRateAndCount(@PathVariable("sellerId") String sellerId) {
		try {
			Map<String, Object> result = updateReviewRateService.getAverageRateAndCount(sellerId);
			// 평균별점과 총 별점 입력 횟수 반환
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			// 예외 발생시 BAD_REQUEST 응답과 함께 에러 메시지 반환
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("별점 통계 계산에 실패했습니다.");
		}
	}

	// 테스트용
	@GetMapping("/health_check")
	public String status() {
		return "user service입니다" + env.getProperty("local.server.port") + ":" + env.getProperty("data.test");
	}
}
