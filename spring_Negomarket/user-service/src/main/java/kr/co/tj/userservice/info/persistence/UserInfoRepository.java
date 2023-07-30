package kr.co.tj.userservice.info.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, String>{

	Optional<UserInfoEntity> findByUsername(String username);

}
