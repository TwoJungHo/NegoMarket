package kr.co.tj.statistic.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.tj.statistic.dto.RateDTO;
import kr.co.tj.statistic.dto.UpdateReviewRateEntity;
import kr.co.tj.statistic.persistence.UpdateReviewRateRepository;

@Service
public class UpdateReviewRateServiceImpl implements UpdateReviewRateService{

	@Autowired
	private UpdateReviewRateRepository updateReviewRateRepository;

	// 별점 입력 (sellerId, rate, rid를 받아야 함. selleId를 기준으로 별점통계를 내고 rid를 기준으로 수정과 삭제 로직이 실행됨.)
	// rid는 review-service의 review게시글의 고유 id. 추후 연동 필요.
	public void leaveRate(String sellerId, RateDTO rateDTO) {
		// 새로운 Entity 생성 후 저장
		UpdateReviewRateEntity newEntity = new UpdateReviewRateEntity();
		newEntity.setSellerId(sellerId);
		newEntity.setRate(rateDTO.getRate()); // 첫 별점을 저장
		newEntity.setCount(1); // count의 초기값을 1로 설정
		newEntity.setRid(rateDTO.getRid()); // rid 저장 (review게시글 고유 id)

		updateReviewRateRepository.save(newEntity);
	}

	// 별점 수정 (rid를 기준으로 별점 수정. 이전에 입력했던 별점기록은 삭제되고 새로운 입력한 별점이 업데이트 됨.)
	public void updateRate(Long rid, RateDTO rateDTO) {
		Optional<UpdateReviewRateEntity> entity = updateReviewRateRepository.findByRid(rid);

		if (entity.isPresent()) {
			UpdateReviewRateEntity rateEntity = entity.get();
			// 전체 별점에서 수정할 별점을 뺀 후 새로운 별점을 더함
			rateEntity.setRate(rateDTO.getRate());
			updateReviewRateRepository.save(rateEntity);
		} else {
			throw new RuntimeException("해당 회원의 별점 정보가 없습니다.");
		}
	}

	// 별점 삭제 (rid를 기준으로 별점 삭제. 별점 삭제시 count도 1씩 줄어듬.)
	public void deleteRate(Long rid, RateDTO rateDTO) {
		Optional<UpdateReviewRateEntity> entity = updateReviewRateRepository.findByRid(rid);

		if (entity.isPresent()) {
			UpdateReviewRateEntity rateEntity = entity.get();
			// 전체 별점에서 삭제할 별점을 뺌
			rateEntity.setRate(rateEntity.getRate() - rateDTO.getRate());
			// count 감소
			rateEntity.setCount(rateEntity.getCount() - 1);
			updateReviewRateRepository.save(rateEntity);
		} else {
			throw new RuntimeException("해당 회원의 별점 정보가 없습니다.");
		}
	}

	// 평균 별점 (sellerId를 기준으로 평균 별점 계산. sellerId에게 입력된 모든 별점의 합 / 총 count 로 계산함.)
	public Map<String, Object> getAverageRateAndCount(String sellerId) {
		// sellerId를 기준으로 모든 RateEntity를 검색
		List<UpdateReviewRateEntity> entities = updateReviewRateRepository.findBySellerId(sellerId);

		if (entities.isEmpty()) {
			// 별점 정보가 없는 경우, 예외처리
			throw new RuntimeException("아직 " + sellerId + "의 별점정보가 없습니다. ");
		} else {
			Map<String, Object> result = new HashMap<>();
			int totalRate = 0; // 총 별점 합계
			int totalCount = 0; // 총 count

			for (UpdateReviewRateEntity entity : entities) {
				totalRate += entity.getRate();
				totalCount += entity.getCount();
			}

			// 별점 평균값 계산 (총 별점 합계 / 총 count)
			result.put("averageRate", (float) totalRate / totalCount);
			// 총 count 반환
			result.put("count", totalCount);

			return result;
		}
	}

}