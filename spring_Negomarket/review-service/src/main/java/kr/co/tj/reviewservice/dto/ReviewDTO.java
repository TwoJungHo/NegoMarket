package kr.co.tj.reviewservice.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Lob;

import kr.co.tj.reviewservice.persistence.ReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id; // 리뷰게시글 번호

	private String sellerName; // 판매자 username
	
	private String sellId;

	private String buyerName; // 구매자 username

	private String content;

	private int count;

	private float rate;

	private Date createDate;

	private Date updateDate;
	

	// 서비스에서 저장한 값을 dto에 넣는작업
	public ReviewDTO toReviewDTO(ReviewEntity entity) {
				this.id = entity.getId();
				this.sellId = entity.getSellId();
				this.sellerName = entity.getSellerName();
				this.buyerName = entity.getBuyerName();
				this.content = entity.getContent();
				this.rate = entity.getRate();
				this.count = entity.getCount();
		
		return this;
	}

	public ReviewEntity toReviewEntity() {
		
		return ReviewEntity.builder()
				.id(id)
				.sellId(sellId)
				.sellerName(sellerName)
				.buyerName(buyerName)
				.content(content)
				.count(count)
				.rate(rate)
				.createDate(createDate)
				.updateDate(updateDate)
				.build();
	}

	// 입력받은 값을 dto에 넣는작업
	public static ReviewDTO toReviewDTOreq(ReviewRequest reviewRequest) {
		
		return ReviewDTO.builder()
				.id(reviewRequest.getId())	//수정을위해 id추가
				.sellId(reviewRequest.getSellId())
				.sellerName(reviewRequest.getSellerName())
				.buyerName(reviewRequest.getBuyerName())
				.content(reviewRequest.getContent())
				.rate(reviewRequest.getRate())
				.build();
	}
	
	// 저장한 dto값을 response에 넘김
	public ReviewResponse toReviewResponse() {
		
		return ReviewResponse.builder()
				.id(id)
				.sellerName(sellerName)
				.sellId(sellId)
				.buyerName(buyerName)
				.content(content)
				.count(count)
				.rate(rate)
				.createDate(createDate)
				.updateDate(updateDate)
				.build();
	

		
	}

	public ReviewResponse toReviewFindResponse(ReviewEntity x) {
		
		return ReviewResponse.builder()
				.id(x.getId())
				.sellId(x.getSellId())
				.sellerName(x.getSellerName())
				.buyerName(x.getBuyerName())
				.content(x.getContent())
				.count(x.getCount())
				.rate(x.getRate())
				.createDate(x.getCreateDate())
				.updateDate(x.getUpdateDate())
				.build();
	}

}
