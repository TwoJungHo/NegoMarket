package kr.co.tj.userservice.info.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.tj.userservice.DataNotFoundException;
import kr.co.tj.userservice.info.dto.UserInfoDTO;
import kr.co.tj.userservice.info.persistence.UserInfoEntity;
import kr.co.tj.userservice.info.persistence.UserInfoRepository;
import kr.co.tj.userservice.sec.TokenProvider;

@Service
public class UserInfoServiceImpl implements UserInfoService {

	private UserInfoRepository userInfoRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private TokenProvider tokenProvider;

	@Autowired
	public UserInfoServiceImpl(UserInfoRepository userInfoRepository, BCryptPasswordEncoder passwordEncoder,
			TokenProvider tokenProvider) {
		super();
		this.userInfoRepository = userInfoRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
	}

	@Override
	public UserInfoDTO login(UserInfoDTO userInfoDTO) {
		Optional<UserInfoEntity> optional = userInfoRepository.findByUsername(userInfoDTO.getUsername());

		if (!optional.isPresent()) {
			return null;
		}

		UserInfoEntity entity = optional.get();

		if (!passwordEncoder.matches(userInfoDTO.getPassword(), entity.getPassword())) {
			return null;
		}

		String token = tokenProvider.create(entity);
		
		userInfoDTO.setLatitude(entity.getLatitude());
		userInfoDTO.setLongitude(entity.getLongitude());
		userInfoDTO.setToken(token);
		userInfoDTO.setPassword("");

		return userInfoDTO;

	}
	
	@Override
	@Transactional
	public boolean passwordValidation(String username, String password) {
		
		
		
		Optional<UserInfoEntity> optional = userInfoRepository.findByUsername(username);

		if (!optional.isPresent()) {
			throw new DataNotFoundException("없는 사용자");
		}

		UserInfoEntity entity = optional.get();
	
		if (!passwordEncoder.matches(password, entity.getPassword())) {
			return false;
		}
		
		

		return true;
	}
	

	// 회원가입
	@Override
	@Transactional
	public UserInfoDTO insertUser(UserInfoDTO userInfoDTO) {
		Date date = new Date();

		String encPassword = passwordEncoder.encode(userInfoDTO.getPassword());
		userInfoDTO.setPassword(encPassword);
		System.err.println(userInfoDTO);
		System.err.println(userInfoDTO);
		System.err.println(userInfoDTO);
		System.err.println(userInfoDTO);
		System.err.println(userInfoDTO);
		System.err.println(userInfoDTO);
		System.err.println(userInfoDTO);
		UserInfoEntity userInfoEntity = UserInfoEntity.builder()
				.username(userInfoDTO.getUsername())
				.password(encPassword)
				.name(userInfoDTO.getName())
				.longitude(userInfoDTO.getLongitude())
				.latitude(userInfoDTO.getLatitude())
				.createAt(date)
				.updateAt(date)
				.build();

		userInfoEntity = userInfoRepository.save(userInfoEntity);
		
		userInfoDTO.setCreateAt(userInfoEntity.getCreateAt());
		userInfoDTO.setUpdateAt(userInfoEntity.getUpdateAt());

		return userInfoDTO;
	}

	// 수정
	@Override
	@Transactional
	public UserInfoDTO updateUser(UserInfoDTO userInfoDTO) {
		Date date = new Date();
		
		String encPassword = passwordEncoder.encode(userInfoDTO.getPassword());
		
		//UserInfoDTO orgDTO = getUser(userInfoDTO.getUsername());
		UserInfoDTO orgDTO = findPassword(userInfoDTO.getUsername());
		UserInfoEntity entity = UserInfoEntity.builder()
				.id(orgDTO.getId())
				.username(orgDTO.getUsername())
				.password(encPassword)
				.name(userInfoDTO.getName())
				.longitude(userInfoDTO.getLongitude())
				.latitude(userInfoDTO.getLatitude())
				.createAt(orgDTO.getCreateAt())
				.updateAt(date)
				.build();

		entity = userInfoRepository.save(entity);
		
		orgDTO = findPassword(entity.getUsername());
		
		return orgDTO;

	}
	
	@Override
	public UserInfoDTO findPassword(String username) {
		
		Optional<UserInfoEntity> optional = userInfoRepository.findByUsername(username);
		UserInfoEntity entity = optional.get();
		System.out.println(entity.getId());
		UserInfoDTO dto = UserInfoDTO.builder()
				.id(entity.getId())
				.username(entity.getUsername())
				.password(entity.getPassword())
				.name(entity.getName())
				.longitude(entity.getLongitude())
				.latitude(entity.getLatitude())
				.createAt(entity.getCreateAt())
				.updateAt(entity.getUpdateAt())
				.build();
		return dto;
	}

	// 삭제
	@Override
	@Transactional
	public void deleteUser(String username) {
		Optional<UserInfoEntity> optional = userInfoRepository.findByUsername(username);
		if (!optional.isPresent()) {
			throw new DataNotFoundException("사용자를 찾을 수 없습니다");
		}

		UserInfoEntity entity = optional.get();
		userInfoRepository.delete(entity);

	}

	// 목록
	@Override
	@Transactional
	public List<UserInfoDTO> getUsers() {
		List<UserInfoEntity> userInfoEntities = userInfoRepository.findAll();
		return userInfoEntities.stream().map(userEntity -> new UserInfoDTO().toUserDTO(userEntity))
				.collect(Collectors.toList());
	}

	// 상세보기
	@Override
	@Transactional
	public UserInfoDTO getUser(String username) {
		Optional<UserInfoEntity> optional = userInfoRepository.findByUsername(username);

		if (!optional.isPresent()) {
			throw new DataNotFoundException("사용자 정보를 찾을 수 없음");
		}

		UserInfoEntity entity = optional.get();

		return new UserInfoDTO().toUserDTO(entity);
	}

	// 날짜정보 주입
	@Override
	public UserInfoDTO setDate(UserInfoDTO userInfoDTO) {
		Date date = new Date();

		if (userInfoDTO.getCreateAt() == null) {
			userInfoDTO.setCreateAt(date);
		}

		userInfoDTO.setUpdateAt(date);
		return userInfoDTO;
	}

	
}
