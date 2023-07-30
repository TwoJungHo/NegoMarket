package kr.co.tj.sellservice.info.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import kr.co.tj.sellservice.SellState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "sell")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellInfoEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "id-uuid")
	@GenericGenerator(strategy = "uuid", name = "id-uuid")
	private String id;
	
	@Column(nullable = false)
	private String username;
	private String buyer;
	@Column(nullable = false)
	private String productName;
	@Column(nullable = false)
	private Long price;
	
	private Date createAt;
	private Date updateAt;
	private Date finishAt;
	@Column(nullable = false)
	private Double longitude;
	@Column(nullable = false)
	private Double latitude;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SellState sellState;
	
	boolean isReviewed;
	
			
	public void setReviewed(boolean isReviewed) {
	    this.isReviewed = isReviewed;
	}
	
	public void setSellState(SellState sellState) {
		this.sellState = sellState;
	}
	
	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}
	
	public void setFinishAt(Date finishAt) {
		this.finishAt = finishAt;
	}
	

}
