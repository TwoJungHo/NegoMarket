package kr.co.tj.reviewservice.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "reviews")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;			// 리뷰게시글 번호
	
	@Column (nullable = false)
	private String sellId;
	
	@Column(nullable = false)
	private String sellerName;	//판매자 username
	
	@Column(nullable = false)
	private String buyerName;	// 구매자 username
	
	@Lob
	@Column(nullable = false)
	private String content;
	
	@Column(nullable = false)
	private int count;
	
	@Column(nullable = false)
	private float rate;
	
	private Date createDate;
	
	private Date updateDate;

}
