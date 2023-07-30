package kr.co.tj.sellservice.img.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.tj.sellservice.DataNotFoundException;
import kr.co.tj.sellservice.img.dto.SellImgDTO;
import kr.co.tj.sellservice.img.persistence.SellImgEntity;
import kr.co.tj.sellservice.img.persistence.SellImgRepository;


@Service
public class SellImgServiceImpl implements SellImgService{
	
	private SellImgRepository sellImgRepository;
	
	
	
	@Autowired
	public SellImgServiceImpl(SellImgRepository sellImgRepository) {
		super();
		this.sellImgRepository = sellImgRepository;
	}
	
	
	@Override
	public SellImgDTO findBySellId(String sellId) {
		
		Optional<SellImgEntity> optional = sellImgRepository.findBySid(sellId);
		
		if(!optional.isPresent()) {
			
			return null;
		}
		
		SellImgEntity sellImgEntity = optional.get();
		
		SellImgDTO sellImgDTO = SellImgDTO.builder()
				.id(sellImgEntity.getId())
				.sid(sellImgEntity.getSid())
				.filename(sellImgEntity.getFilename())
				.imgData(sellImgEntity.getImgData())
				.thumData(sellImgEntity.getThumData())
				.build();
		
		return sellImgDTO;
	}
	
	
	
	

	@Override
	public String insertImg(SellImgDTO imgDTO) {
		
		SellImgEntity entity = SellImgEntity.builder()
				.sid(imgDTO.getSid())
				.filename(imgDTO.getFilename())
				.imgData(imgDTO.getImgData())
				.thumData(imgDTO.getThumData())
				.build();
		
		entity = sellImgRepository.save(entity);
		
		String sid = entity.getSid();
		
		return sid;
		
	}
	
	@Override
	public String updateImg(SellImgDTO imgDTO) {
		
		Optional<SellImgEntity> optional = sellImgRepository.findBySid(imgDTO.getSid());
		
		SellImgEntity sellImgEntity;
		
		if(optional.isPresent()) {
			
			SellImgEntity orgSellImgEntity = optional.get();
			Long orgId = orgSellImgEntity.getId();
			
			sellImgEntity = SellImgEntity.builder()
					.id(orgId)
					.sid(imgDTO.getSid())
					.filename(imgDTO.getFilename())
					.imgData(imgDTO.getImgData())
					.thumData(imgDTO.getThumData())
					.build();
		} else {
			
			sellImgEntity = SellImgEntity.builder()
					.id(null)
					.sid(imgDTO.getSid())
					.filename(imgDTO.getFilename())
					.imgData(imgDTO.getImgData())
					.thumData(imgDTO.getThumData())
					.build();
		}
		
		sellImgEntity = sellImgRepository.save(sellImgEntity);
		
		return sellImgEntity.getSid();
			
	}

	
	@Override
	public void deleteImg(String sellId) {

		Optional<SellImgEntity> optional = sellImgRepository.findBySid(sellId);

		if (!optional.isPresent()) {
			
			return;

		}
		
		SellImgEntity entity = optional.get();

		sellImgRepository.delete(entity);
		
	}
	
	
	


}
