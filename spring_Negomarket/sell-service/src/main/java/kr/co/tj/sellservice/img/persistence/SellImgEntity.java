package kr.co.tj.sellservice.img.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sellimgfile")
public class SellImgEntity {
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String sid;
	
	@Column(nullable = false)
	private String filename;
	
	@Column(columnDefinition = "MediumBLOB")
	private byte[] imgData;
	
	@Column(columnDefinition = "MediumBLOB")
	private byte[] thumData;

}
