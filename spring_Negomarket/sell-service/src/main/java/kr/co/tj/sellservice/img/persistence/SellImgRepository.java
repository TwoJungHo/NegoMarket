package kr.co.tj.sellservice.img.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SellImgRepository extends JpaRepository<SellImgEntity, Long>{

	Optional<SellImgEntity> findBySid(String sid);


}
