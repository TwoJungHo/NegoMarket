package kr.co.tj.imgfileservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import kr.co.tj.imgfileservice.dto.ImgFileDTO;
import kr.co.tj.imgfileservice.persistence.ImgFileEntity;
import kr.co.tj.imgfileservice.persistence.ImgFileRepository;

@Service
public class ImgFileService {
	
	@Autowired
	private ImgFileRepository imgFileRepository;

	public ImgFileDTO uploadImg(ImgFileDTO dto) {
		
		ImgFileEntity entity = ImgFileEntity.builder()
				.filedata(dto.getFiledata())
				.filename(dto.getFilename())
				.build();
		
		entity = imgFileRepository.save(entity);
		
		dto.setId(entity.getId()); 
		
		
		return dto;
	}

	public ImgFileDTO getImgFile(Long id) {
		Optional<ImgFileEntity> optional = imgFileRepository.findById(id);
		
		if(!optional.isPresent()) {
			throw new RuntimeException();
		}
		
		ImgFileEntity entity = optional.get();
		ImgFileDTO dto = new ImgFileDTO(entity.getId(), entity.getFilename(), entity.getFiledata());
				
		return dto;
	}

}
