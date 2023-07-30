package kr.co.tj.sellservice.info.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceInfoRequest {
	
	private Double lng1;
    private Double lat1;
    private Double lng2;
    private Double lat2;

}
