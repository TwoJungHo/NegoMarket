package kr.co.tj.statistic.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "updatereviewsrate")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewRateEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String sellerId;
	
	@Column(nullable = false)
	private Long rid; // review게시글의 고유 id
	
	@Column(nullable = false)
	private float rate;
	
	private int count;

}
