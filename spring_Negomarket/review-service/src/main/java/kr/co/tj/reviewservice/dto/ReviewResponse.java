package kr.co.tj.reviewservice.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Long id; // 리뷰게시글 번호
	
	private String sellId; // sellInfo의 id값

	private String sellerName; // 판매자 username

	private String buyerName; // 구매자 username

	private String content;

	private int count;

	private float rate;

	private Date createDate;

	private Date updateDate;

}
