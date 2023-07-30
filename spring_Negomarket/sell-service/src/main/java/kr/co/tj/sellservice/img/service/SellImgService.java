package kr.co.tj.sellservice.img.service;



import kr.co.tj.sellservice.img.dto.SellImgDTO;

public interface SellImgService {

	String insertImg(SellImgDTO imgDTO);
	String updateImg(SellImgDTO imgDTO);

	SellImgDTO findBySellId(String sellId);
	void deleteImg(String sellId);

	

}
