package kr.co.tj.imgfileservice.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kr.co.tj.imgfileservice.dto.ImgFileDTO;
import kr.co.tj.imgfileservice.service.ImgFileService;

@RestController
@RequestMapping("/imgfile-service")
public class ImgFileController {

	@Autowired
	private Environment env;

	@Autowired
	private ImgFileService imgFileService;

	@PostMapping("/uploadimg")
	public ResponseEntity<?> uploadImg(MultipartHttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();

		MultipartFile file = request.getFile("file");
		//String filename = request.getParameter("filename");
		String filename = file.getOriginalFilename();

		try {

			byte[] filedata = file.getBytes();
			ImgFileDTO dto = new ImgFileDTO(null, filename, filedata);
			dto = imgFileService.uploadImg(dto);
			map.put("result", dto.getId());
			return ResponseEntity.ok().body(map);

		} catch (IOException e) {

			e.printStackTrace();
			map.put("result", "업로드 과정에서 문제 발생");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}

	}
	
	@GetMapping("/getimgdata")
	public ResponseEntity<?> getImg(@RequestParam("id") Long id){
				
		ImgFileDTO dto = imgFileService.getImgFile(id);
		
		String filename = dto.getFilename();
		byte[] filedata = dto.getFiledata();
		
		String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
		MediaType mediaType = MediaType.parseMediaType("image/" + fileExtension);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		
		return ResponseEntity.ok().headers(headers).body(filedata);
	}
	
	@GetMapping("/health_check")
	public String status() {
		return "imgfile-service 입니다" + env.getProperty("local.server.port");
	}
	
	

}
