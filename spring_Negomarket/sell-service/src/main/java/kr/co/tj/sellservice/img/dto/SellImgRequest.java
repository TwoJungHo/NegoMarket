package kr.co.tj.sellservice.img.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SellImgRequest {
	
	private Long id;

	private String sid;
	
	private String filename;

	private byte[] imgData;

	private byte[] thumData;
	
	

}
