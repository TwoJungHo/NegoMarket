package kr.co.tj.userservice.info.service;

import java.util.List;

import kr.co.tj.userservice.info.dto.UserInfoDTO;

public interface UserInfoService {
	
	UserInfoDTO login(UserInfoDTO userInfoDTO);
	List<UserInfoDTO> getUsers();
	UserInfoDTO getUser(String username);
	UserInfoDTO insertUser(UserInfoDTO userInfoDTO);
	UserInfoDTO updateUser(UserInfoDTO userInfoDTO);
	void deleteUser(String username);
	UserInfoDTO setDate(UserInfoDTO userInfoDTO);
	
	boolean passwordValidation(String username, String password);
	
	

}
