package kr.co.tj.userservice.pic.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPicRepository extends JpaRepository<UserPicEntity, Long>{

	Optional<UserPicEntity> findByUsername(String username);

}
