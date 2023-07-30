package kr.co.tj.sellservice.info.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SellInfoRepository extends JpaRepository<SellInfoEntity, String>{

	Optional<SellInfoEntity> findById(String sellId);

	List<SellInfoEntity> findByLongitudeBetweenAndLatitudeBetween(
			Double longitude1, Double longitude2,
			Double latitude1, Double latitude2);

	List<SellInfoEntity> findByUsername(String Username);

}
