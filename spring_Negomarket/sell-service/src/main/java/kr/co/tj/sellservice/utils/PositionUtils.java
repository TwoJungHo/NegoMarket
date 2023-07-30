package kr.co.tj.sellservice.utils;

public class PositionUtils {
	
	
	/* (경도1, 위도1) (경도2, 위도2) 입력 받아서 거리를 계산하는 메서드. 하버사인 포뮬러 기반으로 계산*/
	public static Double calcDistance(Double longitude1, Double latitude1,
			Double longitude2, Double latitude2) {
				
		Double distance;
	    Double radius = 6371.0; // 지구 반지름(km)
	    Double toRadian = Math.PI / 180;

	    Double lng1 = longitude1 * toRadian;
	    Double lat1 = latitude1 * toRadian;
	    Double lng2 = longitude2 * toRadian;
	    Double lat2 = latitude2 * toRadian;
	    
	    Double deltaLat = lat2 - lat1;
	    Double deltaLng = lng2 - lng1;
	    
	    
	    Double sinDeltaLat = Math.sin(deltaLat / 2);
	    Double sinDeltaLng = Math.sin(deltaLng / 2);
	    Double squareRoot = Math.sqrt(
	        (sinDeltaLat * sinDeltaLat) +
	        (Math.cos(lat1) * Math.cos(lat2) * sinDeltaLng * sinDeltaLng));

	    distance = 2 * radius * Math.asin(squareRoot);

	    return distance;
	}

}
