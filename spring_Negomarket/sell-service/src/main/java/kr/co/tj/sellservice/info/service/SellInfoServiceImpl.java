package kr.co.tj.sellservice.info.service;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import kr.co.tj.sellservice.DataNotFoundException;
import kr.co.tj.sellservice.SellState;
import kr.co.tj.sellservice.UneditableConditonException;

import kr.co.tj.sellservice.info.dto.SellInfoDTO;
import kr.co.tj.sellservice.info.persistence.SellInfoEntity;
import kr.co.tj.sellservice.info.persistence.SellInfoRepository;
import kr.co.tj.sellservice.utils.PositionUtils;


@Service
public class SellInfoServiceImpl implements SellInfoService {
	
	private SellInfoRepository sellInfoRepository;
	
	@Autowired
	public SellInfoServiceImpl(SellInfoRepository sellInfoRepository) {
		super();
		this.sellInfoRepository = sellInfoRepository;
	}
	
	@Override
	public SellInfoDTO findBySellId(String sellId) {
		
		Optional<SellInfoEntity> optional = sellInfoRepository.findById(sellId);
		SellInfoEntity sellInfoEntity = optional.get();
		
		SellInfoDTO sellInfoDTO = SellInfoDTO.builder()
				.id(sellInfoEntity.getId())
				.username(sellInfoEntity.getUsername())
				.productName(sellInfoEntity.getProductName())
				.price(sellInfoEntity.getPrice())
				.buyer(sellInfoEntity.getBuyer())
				.createAt(sellInfoEntity.getCreateAt())
				.updateAt(sellInfoEntity.getUpdateAt())
				.finishAt(sellInfoEntity.getFinishAt())
				.longitude(sellInfoEntity.getLongitude())
				.latitude(sellInfoEntity.getLatitude())
				.sellState(sellInfoEntity.getSellState())
				.isReviewed(sellInfoEntity.isReviewed())
				.build();
				
		return sellInfoDTO;
	}
	
	@Override
	public List<SellInfoDTO> findByUsername(String username) {
		
		
		List<SellInfoEntity> entityList = sellInfoRepository.findByUsername(username);
		List<SellInfoDTO> dtoList = new ArrayList<>();
		for (SellInfoEntity entity: entityList) {
			SellInfoDTO sellInfoDTO = SellInfoDTO.builder()
					.id(entity.getId())
					.username(entity.getUsername())
					.productName(entity.getProductName())
					.price(entity.getPrice())
					.buyer(entity.getBuyer())
					.createAt(entity.getCreateAt())
					.updateAt(entity.getUpdateAt())
					.finishAt(entity.getFinishAt())
					.longitude(entity.getLongitude())
					.latitude(entity.getLatitude())
					.sellState(entity.getSellState())
					.isReviewed(entity.isReviewed())
					.build();
			dtoList.add(sellInfoDTO);
		}
		
		return dtoList;
	}
	
	
	@Override
	public SellInfoDTO insert(SellInfoDTO dto) {
			
		Date date = new Date();
		SellInfoEntity entity = SellInfoEntity.builder()
			
				.username(dto.getUsername())
				.buyer(dto.getBuyer())
				.productName(dto.getProductName())
				.price(dto.getPrice())
				.createAt(date)
				.updateAt(date)
				.longitude(dto.getLongitude())
				.latitude(dto.getLatitude())
				.sellState(SellState.ON_SALE)
				.build();
		
		entity = sellInfoRepository.save(entity);
		
		dto.setId(entity.getId());
		dto.setSellState(entity.getSellState());
		dto.setCreateAt(entity.getCreateAt());
		dto.setUpdateAt(entity.getUpdateAt());
		
		return dto;
	}
	
	@Override
	public SellInfoDTO update(SellInfoDTO dto) {
		
		Optional<SellInfoEntity> optional = sellInfoRepository.findById(dto.getId());
		
		if(!optional.isPresent()) {
			throw new DataNotFoundException("존재하지 않는 데이터 입니다");
		}
				
		
		SellInfoEntity orgEntity = optional.get();
		
		if(orgEntity.isReviewed()) {
			throw new UneditableConditonException("리뷰가 작성된 후 수정 불가");
		}
		
		Date date = new Date();
		Date finishAt = null;
		
		// 본래 state가 sold_out이 아닌데, 업데이트 값이 sold_out으로 올 경우 종료 날짜 지정
		if(orgEntity.getSellState() != SellState.SOLD_OUT && dto.getSellState() == SellState.SOLD_OUT) {
			finishAt = date;
		}
		
		if(dto.getSellState() == null) {
			dto.setSellState(orgEntity.getSellState());
		}
		
			
		SellInfoEntity entity = SellInfoEntity.builder()
				.id(orgEntity.getId())
				.username(orgEntity.getUsername())
				.createAt(orgEntity.getCreateAt())
				.buyer(dto.getBuyer())
				.productName(dto.getProductName())
				.price(dto.getPrice())
				.updateAt(date)
				.finishAt(finishAt)
				.longitude(dto.getLongitude())
				.latitude(dto.getLatitude())
				.sellState(dto.getSellState())
		
				.build();
		
		entity = sellInfoRepository.save(entity);
		
		dto.setUpdateAt(entity.getUpdateAt());
		dto.setFinishAt(entity.getFinishAt());
				
		return dto;
	}
	
	
	/* sellstate에 따라 sell 데이터 삭제 여부를 결정하고, sell image가 삭제되어야 하는지 여부(boolean)를
	 * return 해줌
	 */
	@Override
	public boolean delete(String id) {
		Optional<SellInfoEntity> optional = sellInfoRepository.findById(id);

		if (!optional.isPresent()) {
			throw new DataNotFoundException("존재하지 않는 데이터 입니다");
		}
		
		SellInfoEntity entity = optional.get();
		
		if(entity.getSellState() == SellState.SOLD_OUT) {
			return false;
		}
		
		sellInfoRepository.delete(entity);
		return true;
		
	}
	

	@Override
	public SellInfoDTO reserve(String id, String buyer) {
		Optional<SellInfoEntity> optional = sellInfoRepository.findById(id);
		
		if (!optional.isPresent()) {
			throw new DataNotFoundException("존재하지 않는 데이터 입니다");
		}

		SellInfoEntity entity = optional.get();
		entity.setBuyer(buyer);
		entity.setSellState(SellState.RESERVED);
		
		entity = sellInfoRepository.save(entity);
		
		SellInfoDTO dto = SellInfoDTO.builder()
				.id(entity.getId())
				.username(entity.getUsername())
				.buyer(entity.getBuyer())
				.productName(entity.getProductName())
				.price(entity.getPrice())
				.createAt(entity.getCreateAt())
				.updateAt(entity.getUpdateAt())
				.finishAt(entity.getFinishAt())
				.longitude(entity.getLongitude())
				.latitude(entity.getLatitude())
				.sellState(entity.getSellState())
				.isReviewed(entity.isReviewed())
				.build();
		
		
		return dto;
	}
	
	@Override
	public SellInfoDTO soldout(String id, String buyer) {
		
		Optional<SellInfoEntity> optional = sellInfoRepository.findById(id);
		
		if (!optional.isPresent()) {
			throw new DataNotFoundException("존재하지 않는 데이터 입니다");
		}

		SellInfoEntity entity = optional.get();
		entity.setBuyer(buyer);
		entity.setSellState(SellState.SOLD_OUT);
		
		Date date = new Date();
		entity.setFinishAt(date);
		
		entity = sellInfoRepository.save(entity);
		
		SellInfoDTO dto = SellInfoDTO.builder()
				.id(entity.getId())
				.username(entity.getUsername())
				.buyer(entity.getBuyer())
				.productName(entity.getProductName())
				.price(entity.getPrice())
				.createAt(entity.getCreateAt())
				.updateAt(entity.getUpdateAt())
				.finishAt(entity.getFinishAt())
				.longitude(entity.getLongitude())
				.latitude(entity.getLatitude())
				.sellState(entity.getSellState())
				.isReviewed(entity.isReviewed())
				.build();
				
		return dto;
	}
	
	
	@Override
	public SellInfoDTO onsale(String id) {
		Optional<SellInfoEntity> optional = sellInfoRepository.findById(id);
		if (!optional.isPresent()) {
			throw new DataNotFoundException("존재하지 않는 데이터 입니다");
		}
		
		SellInfoEntity entity = optional.get();
		
		if(entity.getSellState() == SellState.SOLD_OUT) {
			throw new UneditableConditonException("더 이상 수정할 수 없음");
		}
		
		entity.setBuyer(null);
		entity.setSellState(SellState.ON_SALE);
		
		entity = sellInfoRepository.save(entity);
		
		SellInfoDTO dto = SellInfoDTO.builder()
				.id(entity.getId())
				.username(entity.getUsername())
				.buyer(entity.getBuyer())
				.productName(entity.getProductName())
				.price(entity.getPrice())
				.createAt(entity.getCreateAt())
				.updateAt(entity.getUpdateAt())
				.finishAt(entity.getFinishAt())
				.longitude(entity.getLongitude())
				.latitude(entity.getLatitude())
				.sellState(entity.getSellState())
				.isReviewed(entity.isReviewed())
				.build();
				
		return dto;

	}
	
	
	
	// state만 변경하는 경우에 활용할 수 있는 메서드
	
	
	
	@Override
	public SellInfoDTO isReviewed(String id, boolean isReviewed) {
		
		Optional<SellInfoEntity> optional = sellInfoRepository.findById(id);
		
		if(!optional.isPresent()) {
			throw new DataNotFoundException("존재하지 않는 데이터 입니다");
		}
						
		SellInfoEntity entity = optional.get();
		
		if(entity.getSellState() != SellState.SOLD_OUT) {
			throw new UneditableConditonException("종료 되지 않은 거래");
		}
		
		entity.setReviewed(isReviewed);
				
		entity = sellInfoRepository.save(entity);
		
		SellInfoDTO dto = SellInfoDTO.builder()
				.id(entity.getId())
				.username(entity.getUsername())
				.createAt(entity.getCreateAt())
				.buyer(entity.getBuyer())
				.productName(entity.getProductName())
				.price(entity.getPrice())
				.updateAt(entity.getUpdateAt())
				.finishAt(entity.getFinishAt())
				.longitude(entity.getLongitude())
				.latitude(entity.getLatitude())
				.isReviewed(entity.isReviewed())
				.build();
		
		
		return dto;
				
	}
	
	


	@Override
	public void testinsert(int trialNum, double rangeInKm) {
		
		for (int i = 0; i < trialNum; i++) {
			
			//신촌역 좌표
		    double baseLatitude = 37.5552314845079;  // 기준 위도
		    double baseLongitude = 126.93693998959822;  // 기준 경도
		        	    
		    
		    // 대략적으로 일정범위(사각형 비슷하게) 좌표값 생성, 할당
		    double latitude = (baseLatitude - 0.2) + Math.random() * 0.4;
		    double longitude = (baseLongitude - 0.2) + Math.random() * 0.4;
		    
		    // 미리 마련해둔 calcDistance 메서드로 거리를 측정, 범위 내인지 확인
		    // 대충 사각형 비슷한 범위의 값들 중 거리 밖인 것들은 버려야 함.
		    boolean isGood = PositionUtils.calcDistance(baseLongitude, baseLatitude, longitude, latitude) <= rangeInKm;
		    
		    // 해당 좌표값이 신촌역으로부터 설정한 범위 내가 아니면 다음 i를 시도
		    if(!isGood) {
		    	continue;
		    }
			
			// 범위 내의 값이 설정되었다면 현재 i를 기준으로 아래 코드가 실행됨.
			
		   
		    
		    String sti = String.format("%04d", i);
		    
		    String username = "판매자" + sti;
			String buyer = "구매자" + sti;
			
			String productName = null;
			Long price = null;
			
			//5가지 상품 종류를 마련했습니다!!
			int caseNum = i%5;
			
			switch (caseNum) {
			case 0:
				productName = "맥북 프로" + sti;
				price = 3000000L + i;
				break;
			case 1:
				productName = "신발" + sti;
				price = 120000L + i;
				break;
			case 2:
				productName = "양말" + sti;
				price = 5000L - i;
				break;
			case 3:
				productName = "플레이스테이션" + sti;
				price = 1000000L + i;
				break;
			case 4:
				productName = "아이폰" + sti;
				price = 1350000L + i;
				break;
			
			}
			
			// 날짜도 적당히 랜덤으로 만들어 냄
			Random rand = new Random();
			int year = rand.nextInt(3) + 2021;
			int month = rand.nextInt(12) + 1;
			int day = rand.nextInt(28) + 1;
			Calendar cal = Calendar.getInstance();
			cal.set(year, month-1, day);
			Date date = cal.getTime();
			
			Date createAt = date;
			Date updateAt = date;
			
			
			// enum 설정 방법, 혹시 모르셨던 분은 참고!
			SellState sellState = SellState.ON_SALE;
			
			// 드디어 변수들을 다 마련했습니다. Entity 만들어서 저장합시다.
			
			SellInfoEntity entity = SellInfoEntity.builder()
					.username(username)
					.buyer(buyer)
					.productName(productName)
					.price(price)
					.createAt(createAt)
					.updateAt(updateAt)
					.longitude(longitude)
					.latitude(latitude)
					.sellState(sellState)
					.isReviewed(false)
					.build();
	  		
			sellInfoRepository.save(entity);    
		
		
		} // 여기가 for 문의 끝
		
	}

	@Override
	public List<SellInfoDTO> findAroundAll(Double longitude, Double latitude, Double rangeInKm) {
		Double longitude1 = longitude - 0.15;
		Double longitude2 = longitude + 0.15;
		Double latitude1 = latitude - 0.2;
		Double latitude2 = latitude + 0.2;
				
		List<SellInfoEntity> sellInfoEntityList = sellInfoRepository.findByLongitudeBetweenAndLatitudeBetween(longitude1, longitude2, latitude1, latitude2);
		List<SellInfoDTO> sellInfoDTOList = new ArrayList<>();
		
		for(int i = 0; i < sellInfoEntityList.size(); i++) {
			SellInfoEntity sellInfoEntity = sellInfoEntityList.get(i);
			
			Double lng = sellInfoEntity.getLongitude();
			Double lat = sellInfoEntity.getLatitude();
			
			boolean isInRange = PositionUtils.calcDistance(longitude, latitude, lng, lat) <= rangeInKm;	
			 
			if(!isInRange) {
				
			    	continue;
			}
			
			
			SellInfoDTO sellInfoDTO = SellInfoDTO.builder()
					.id(sellInfoEntity.getId())
					.username(sellInfoEntity.getUsername())
					.productName(sellInfoEntity.getProductName())
					.price(sellInfoEntity.getPrice())
					.createAt(sellInfoEntity.getCreateAt())
					.updateAt(sellInfoEntity.getUpdateAt())
					.finishAt(sellInfoEntity.getFinishAt())
					.longitude(sellInfoEntity.getLongitude())
					.latitude(sellInfoEntity.getLatitude())
					.sellState(sellInfoEntity.getSellState())
					.isReviewed(sellInfoEntity.isReviewed())
					.build();
			
			sellInfoDTOList.add(sellInfoDTO);
		}
		
		return sellInfoDTOList;
	}

	
	
	

}
