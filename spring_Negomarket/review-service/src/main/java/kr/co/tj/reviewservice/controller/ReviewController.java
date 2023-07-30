package kr.co.tj.reviewservice.controller;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.jsonwebtoken.Jwts;
import kr.co.tj.reviewservice.dto.ReviewDTO;
import kr.co.tj.reviewservice.dto.ReviewRequest;
import kr.co.tj.reviewservice.dto.ReviewResponse;
import kr.co.tj.reviewservice.feign.ReviewRateFeign;
import kr.co.tj.reviewservice.feign.SellFeign;
import kr.co.tj.reviewservice.sellDTO.SellFeignResponse;
import kr.co.tj.reviewservice.sellDTO.SellInfoDTO;
import kr.co.tj.reviewservice.sellDTO.SellInfoRequest;
import kr.co.tj.reviewservice.sellDTO.SellInfoResponse;
import kr.co.tj.reviewservice.service.ReviewService;


@RestController
@RequestMapping("review-service")
public class ReviewController {

	private  ReviewService reviewService;
	private  ReviewRateFeign reviewRateFeign;
	private  SellFeign sellFeign;
	private Environment env;
	
	@Autowired
	public ReviewController(ReviewService reviewService, ReviewRateFeign reviewRateFeign, Environment env, SellFeign sellFeign) {
		super();
		this.reviewService = reviewService;
		this.reviewRateFeign = reviewRateFeign;
		this.sellFeign = sellFeign;
		this.env = env;
		
	}
	
	



	// 페이징 구현
	@GetMapping("/page/{page}")
	public ResponseEntity<?> getpage(@PathVariable("page") Integer page){
		page = page - 1;
		Pageable pageable = PageRequest.of(page, 10);
		List<ReviewResponse> list = reviewService.getPage(pageable);

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	

	// 판매자 리뷰 자세히 보기
	@GetMapping("/review/id/{id}")
	public ResponseEntity<?> findById(@PathVariable("id") Long id) {

		try {
			ReviewResponse reviewResponse = reviewService.findById(id);
			return ResponseEntity.status(HttpStatus.OK).body(reviewResponse);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리뷰게시물 정보가없어 불러오지 못했습니다");
		}
	}

	// 판매자의 리뷰 검색해서 모두 보기
	@GetMapping("/review/sellerFindReview")
	public ResponseEntity<?> findBySeller(@RequestParam(name = "keyword") String keyword) {

		if (keyword == null || keyword == "") {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("판매자 정보가 없습니다");
		}

		List<ReviewResponse> list = reviewService.findBySeller(keyword);

		if (list.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리뷰가없습니다.2");
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	// 리뷰 모두 보기
	@GetMapping("/review")
	public ResponseEntity<?> findAll() {

		List<ReviewResponse> list = reviewService.findAll();

		if (list.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리뷰가없습니다.");
		}

		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	// 리뷰 입력
	@PostMapping("/create")
	public ResponseEntity<?> create(@RequestHeader("Authorization") String bearerToken ,MultipartHttpServletRequest request) {

		System.out.println(bearerToken);
		
		
		if(bearerToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		String token = bearerToken.replace("Bearer ", "");
		System.out.println(token);
		String seckey = env.getProperty("data.SECRET_KEY");
		String encodedSeckey = Base64.getEncoder().encodeToString(seckey.getBytes());
		
		

		String username = Jwts.parser()
				.setSigningKey(encodedSeckey)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		
		SellInfoRequest sellInfoRequest = SellInfoRequest.builder()
		.buyer(username)
		.id(request.getParameter("sellId"))
		.isReviewed(true)
		.build();
		
		ResponseEntity<SellFeignResponse> feignResponse = sellFeign.updateIsReviewed(sellInfoRequest);
		if(feignResponse.getStatusCode() != HttpStatus.OK) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
				
		ReviewRequest reviewRequest = ReviewRequest.builder()
				.content(request.getParameter("content"))
				.sellerName(request.getParameter("sellerName"))
				.buyerName(username)
				.sellId(request.getParameter("sellId"))
				.rate(Float.parseFloat(request.getParameter("rate")))
				.build();
		
		if (reviewRequest.getContent() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력된 값이 모두 채워지지 않았습니다.");
		}
		
		ReviewDTO dto = ReviewDTO.toReviewDTOreq(reviewRequest);

		dto = reviewService.createReview(dto);
		ReviewResponse reviewResponse = dto.toReviewResponse();

		return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponse);
	}

	// 리뷰 수정
	@PutMapping("/update")
	public ResponseEntity<?> update(@RequestBody ReviewRequest reviewRequest) {

		if (reviewRequest.getId() == null || reviewRequest.getId().equals("")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 정보가 없습니다.");
		}
		
		
		if (reviewRequest.getContent() == null || reviewRequest.getContent() == "") {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 정보가 없습니다.");
		}

		ReviewDTO dto = ReviewDTO.toReviewDTOreq(reviewRequest);

		dto = reviewService.updateReview(dto);

		if (dto == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리뷰에 대한 글이없습니다.");
		}

		ReviewResponse reviewResponse = dto.toReviewResponse();

		return ResponseEntity.status(HttpStatus.OK).body(reviewResponse);
	}

	// 리뷰 삭제
	@DeleteMapping("/delete")
	public ResponseEntity<?> delete(@RequestBody ReviewRequest reviewRequest) {
		
		try {
			reviewService.delete(reviewRequest.getId());

			return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 게시글이 없습니다.");
		}
	}
	
	// 다중 삭제 추가
	   @DeleteMapping("/delete/multi")
	   public ResponseEntity<?> deleteBoard(@RequestParam("id") String idList) {
		   
	      // 쉼표로 구분된 문자열을 배열로 변환
	      String[] ids = idList.split(",");

	      try {
	         // 배열의 각 요소를 숫자로 변환하여 삭제 작업 수행
	         for (String idStr : ids) {
	            long id = Long.parseLong(idStr);
	            reviewService.delete(id);
	         }

	         return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");

	      } catch (Exception e) {
	         e.printStackTrace();
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 게시글이 없습니다.");
	      }
	   }
	
	
	
	
	
	
	
	
	
	
//	// 리뷰 많이 입력
//	@PostMapping("/testinsert")
//	public void testinsert() {
//		
//		Random rand = new Random();
//		
//		for(int i = 1; i<500; i++) {
//			String sti = String.format("%04d", i);
//			int year = rand.nextInt(3) + 2021;
//			int month = rand.nextInt(12) + 1;
//			int day = rand.nextInt(28) + 1;
//			Calendar cal = Calendar.getInstance();
//			cal.set(year, month-1, day);
//			Date date = cal.getTime();
//			
//			
//			reviewService.testinsert(dto);
//		}
//	}
}
