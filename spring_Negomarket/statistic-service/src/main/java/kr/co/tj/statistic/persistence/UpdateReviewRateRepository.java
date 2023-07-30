package kr.co.tj.statistic.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.tj.statistic.dto.UpdateReviewRateEntity;

@Repository
public interface UpdateReviewRateRepository extends JpaRepository<UpdateReviewRateEntity, Long> {

	Optional<UpdateReviewRateEntity> findByRid(Long rid);

	List<UpdateReviewRateEntity> findBySellerId(String sellerId);

}
