package kr.co.tj.reviewservice.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import kr.co.tj.reviewservice.dto.ReviewDTO;
import kr.co.tj.reviewservice.dto.ReviewResponse;

public interface ReviewService {
	
	ReviewDTO updateReview(ReviewDTO dto);
	ReviewDTO createReview(ReviewDTO dto);
	ReviewDTO getDate(ReviewDTO dto);
	List<ReviewResponse> findAll();
	List<ReviewResponse> findBySeller(String sellerId);
	ReviewResponse findById(Long id);
	void delete(Long id);
	List<ReviewResponse> getPage(Pageable pageable);
	void testinsert(ReviewDTO dto);

}
