package kr.co.tj.userservice.pic.service;

import kr.co.tj.userservice.pic.dto.UserPicDTO;

public interface UserPicService {
	
	String insertPic(UserPicDTO userPicDTO);
	String updatePic(UserPicDTO userPicDTO);
	
	UserPicDTO findByUsername(String username);

}
