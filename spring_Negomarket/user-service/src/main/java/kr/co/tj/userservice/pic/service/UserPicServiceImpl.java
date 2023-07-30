package kr.co.tj.userservice.pic.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.tj.userservice.pic.dto.UserPicDTO;
import kr.co.tj.userservice.pic.persistence.UserPicEntity;
import kr.co.tj.userservice.pic.persistence.UserPicRepository;

@Service
public class UserPicServiceImpl implements UserPicService{
	
	private UserPicRepository userPicRepository;
	
	@Autowired
	public UserPicServiceImpl(UserPicRepository userPicRepository) {
		super();
		this.userPicRepository = userPicRepository;
	}

	@Override
	public String insertPic(UserPicDTO userPicDTO) {
		
		UserPicEntity userPicEntity = UserPicEntity.builder()
				.username(userPicDTO.getUsername())
				.filename(userPicDTO.getFilename())
				.picData(userPicDTO.getPicData())
				.build();
		userPicEntity = userPicRepository.save(userPicEntity);
		String username = userPicEntity.getUsername();
		
		return username;
	}

	@Override
	public String updatePic(UserPicDTO userPicDTO) {
		Optional<UserPicEntity> optional = userPicRepository.findByUsername(userPicDTO.getUsername());
		
		UserPicEntity userPicEntity;
		if(optional.isPresent()) {
			UserPicEntity orgUserPicEntity = optional.get();
			Long orgId = orgUserPicEntity.getId();
			
			userPicEntity = UserPicEntity.builder()
					.id(orgId)
					.username(userPicDTO.getUsername())
					.filename(userPicDTO.getFilename())
					.picData(userPicDTO.getPicData())
					.build();
		} else {
			userPicEntity = UserPicEntity.builder()
					.id(null)
					.username(userPicDTO.getUsername())
					.filename(userPicDTO.getFilename())
					.picData(userPicDTO.getPicData())
					.build();
		}
		
		userPicEntity = userPicRepository.save(userPicEntity);
		
		return userPicEntity.getUsername();
	}

	@Override
	public UserPicDTO findByUsername(String username) {
		
		Optional<UserPicEntity> optional = userPicRepository.findByUsername(username);
		
		if(!optional.isPresent()) {
			return null;
		}
		
		UserPicEntity userPicEntity = optional.get();
		
		UserPicDTO userPicDTO = UserPicDTO.builder()
				.id(userPicEntity.getId())
				.username(userPicEntity.getUsername())
				.filename(userPicEntity.getFilename())
				.picData(userPicEntity.getPicData())
				.build();
		return userPicDTO;
	}
	
	

}
