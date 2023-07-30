package kr.co.tj.reviewservice.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

	List<ReviewEntity> findBySellerName(String sellerName);

}
