package kr.co.tj.sellservice.controller;


import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import kr.co.tj.sellservice.SellState;
import kr.co.tj.sellservice.img.dto.SellImgDTO;
import kr.co.tj.sellservice.img.dto.SellImgRequest;
import kr.co.tj.sellservice.img.service.SellImgService;
import kr.co.tj.sellservice.info.dto.SellInfoDTO;
import kr.co.tj.sellservice.info.dto.SellInfoRequest;
import kr.co.tj.sellservice.info.dto.SellInfoResponse;
import kr.co.tj.sellservice.info.service.SellInfoService;
import kr.co.tj.sellservice.tofeign.SellFeignRequest;
import kr.co.tj.sellservice.tofeign.SellFeignResponse;
import kr.co.tj.sellservice.utils.ImageResize;


@RestController
@RequestMapping("/sell-service")
public class SellController {
	
	private Environment env;
	private SellInfoService sellInfoService;
	private SellImgService sellImgService;
	
	@Autowired
	public SellController(Environment env, SellInfoService sellInfoService, SellImgService sellImgService) {
		super();
		this.env = env;
		this.sellInfoService = sellInfoService;
		this.sellImgService = sellImgService;
	}
	
	// sell 데이터 1개 호출
	@GetMapping("/sells/detail/{sellId}")
	public ResponseEntity<?> findBySellId(@PathVariable("sellId") String sellId) {
		SellInfoDTO sellInfoDTO = sellInfoService.findBySellId(sellId);
		SellImgDTO sellImgDTO = sellImgService.findBySellId(sellId);

		SellInfoResponse sellInfoResponse = SellInfoResponse.builder()
				.id(sellInfoDTO.getId())
				.username(sellInfoDTO.getUsername())
				.buyer(sellInfoDTO.getBuyer())
				.productName(sellInfoDTO.getProductName())
				.price(sellInfoDTO.getPrice())
				.createAt(sellInfoDTO.getCreateAt())
				.updateAt(sellInfoDTO.getUpdateAt())
				.finishAt(sellInfoDTO.getFinishAt())
				.longitude(sellInfoDTO.getLongitude())
				.latitude(sellInfoDTO.getLatitude())
				.sellState(sellInfoDTO.getSellState())
				.isReviewed(sellInfoDTO.isReviewed())
				.build();

		if (sellImgDTO != null) {
			SellResponse sellResponse = SellResponse.builder()
					.sellInfoResponse(sellInfoResponse)
					.imgPathId(sellImgDTO.getId())
					.build();

			return ResponseEntity.ok().body(sellResponse);
		}

		SellResponse sellResponse = SellResponse.builder()
				.sellInfoResponse(sellInfoResponse)
				.imgPathId(null)
				.build();
		return ResponseEntity.ok().body(sellResponse);
	}
	
		
	@GetMapping("/sells/mylist")
	public ResponseEntity<?> mysells(@RequestHeader(value = "Authorization", required = false) String bearerToken) {

		if (bearerToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		}

		String token = bearerToken.replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());

		String username;

		try {
			username = Jwts.parser()
					.setSigningKey(encodedSecKey)
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
		} catch (ExpiredJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (UnsupportedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (MalformedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (SignatureException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		List<SellInfoDTO> dtoList = sellInfoService.findByUsername(username);

		List<SellInfoResponse> responseList = new ArrayList<>();
		for (SellInfoDTO dto : dtoList) {
			SellInfoResponse response = SellInfoResponse.builder()
					.id(dto.getId())
					.username(dto.getUsername())
					.productName(dto.getProductName())
					.price(dto.getPrice())
					.buyer(dto.getBuyer())
					.createAt(dto.getCreateAt())
					.updateAt(dto.getUpdateAt())
					.finishAt(dto.getFinishAt())
					.longitude(dto.getLongitude())
					.latitude(dto.getLatitude())
					.sellState(dto.getSellState())
					.isReviewed(dto.isReviewed())
					.build();
			responseList.add(response);
		}

		return ResponseEntity.ok().body(responseList);
	}
	
	
	@GetMapping("/img/thum/{sellId}")
	public ResponseEntity<?> getSellThum(@PathVariable("sellId") String sellId){
		SellImgDTO sellImgDTO = sellImgService.findBySellId(sellId);
		if(sellImgDTO == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		String filename = sellImgDTO.getFilename();
		byte[] thumData = sellImgDTO.getThumData();
		
		String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
		MediaType mediaType = MediaType.parseMediaType("image/" + fileExtension);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		
		return ResponseEntity.ok().headers(headers).body(thumData);
	}
	
	@GetMapping("/img/{sellId}")
	public ResponseEntity<?> getSellImg(@PathVariable("sellId") String sellId){
		
		SellImgDTO sellImgDTO = sellImgService.findBySellId(sellId);
		
		if(sellImgDTO == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		String filename = sellImgDTO.getFilename();
		byte[] imgData = sellImgDTO.getImgData();
		
		String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
		MediaType mediaType = MediaType.parseMediaType("image/" + fileExtension);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		
		return ResponseEntity.ok().headers(headers).body(imgData);
	}
	
	/* board-service에서 feign으로 호출되는 엔드포인트
	 * 각각 board의 Get, Post, Put, Delete 요청에 대응해서
	 * sell 데이터를 처리하게 됨.	 */
	@GetMapping("/board-sells/{sellId}")
	public ResponseEntity<?> getSell(@PathVariable("sellId") String sellId){
		SellInfoDTO sellInfoDTO = sellInfoService.findBySellId(sellId);
		SellImgDTO sellImgDTO = sellImgService.findBySellId(sellId);
		
		SellInfoResponse sellInfoResponse = SellInfoResponse.builder()
				.id(sellInfoDTO.getId())
				.username(sellInfoDTO.getUsername())
				.buyer(sellInfoDTO.getBuyer())
				.productName(sellInfoDTO.getProductName())
				.price(sellInfoDTO.getPrice())
				.createAt(sellInfoDTO.getCreateAt())
				.updateAt(sellInfoDTO.getUpdateAt())
				.finishAt(sellInfoDTO.getFinishAt())
				.longitude(sellInfoDTO.getLongitude())
				.latitude(sellInfoDTO.getLatitude())
				.sellState(sellInfoDTO.getSellState())
				.isReviewed(sellInfoDTO.isReviewed())
				.build();
		
		if(sellImgDTO != null) {
		SellResponse sellResponse = SellResponse.builder()
		.sellInfoResponse(sellInfoResponse)
		.imgPathId(sellImgDTO.getId())
		.build();
		return ResponseEntity.ok().body(sellResponse);
		}
		
		SellResponse sellResponse = SellResponse.builder()
				.sellInfoResponse(sellInfoResponse)
				.imgPathId(null)
				.build();
		
		return ResponseEntity.ok().body(sellResponse);			
	}
	
	
	@PostMapping("/board-sells")
	public ResponseEntity<?> insert(@RequestBody SellFeignRequest sellFeignRequest){
		
		SellInfoRequest sellInfoRequest = sellFeignRequest.getSellInfoRequest();
		SellImgRequest sellImgRequest = sellFeignRequest.getSellImgRequest();
						
		SellInfoDTO infoDTO = SellInfoDTO.builder()
				.username(sellInfoRequest.getUsername())
				.productName(sellInfoRequest.getProductName())
				.price(sellInfoRequest.getPrice())
				.longitude(sellInfoRequest.getLongitude())
				.latitude(sellInfoRequest.getLatitude())
				.sellState(sellInfoRequest.getSellState())
				.build();
				
		infoDTO = sellInfoService.insert(infoDTO);
		
		SellInfoResponse infoResponse = SellInfoResponse.builder()
				.id(infoDTO.getId())
				.username(infoDTO.getUsername())
				.productName(infoDTO.getProductName())
				.price(infoDTO.getPrice())
				.createAt(infoDTO.getCreateAt())
				.updateAt(infoDTO.getUpdateAt())
				.longitude(infoDTO.getLongitude())
				.latitude(infoDTO.getLatitude())
				.sellState(infoDTO.getSellState())
				.build();
		
		if(sellImgRequest == null) {
			SellFeignResponse sellFeignResponse = new SellFeignResponse(infoResponse, "there was no sell image");
			return ResponseEntity.ok().body(sellFeignResponse);		
		}
		
		SellImgDTO imgDTO = SellImgDTO.builder()
				.sid(infoResponse.getId())
				.filename(sellImgRequest.getFilename())
				.imgData(sellImgRequest.getImgData())
				.thumData(sellImgRequest.getThumData())
				.build();
		String sid = sellImgService.insertImg(imgDTO);
		SellFeignResponse sellFeignResponse = new SellFeignResponse(infoResponse, "sell image successfully uploaded with id: " + sid);
		return ResponseEntity.ok().body(sellFeignResponse);
	}
	
	
	@PutMapping("/board-sells/{sellId}")
	public ResponseEntity<?> update(@PathVariable("sellId") String sellId,
			@RequestBody SellFeignRequest sellFeignRequest) {

		SellInfoRequest sellInfoRequest = sellFeignRequest.getSellInfoRequest();
		SellImgRequest sellImgRequest = sellFeignRequest.getSellImgRequest();

		SellInfoDTO infoDTO = SellInfoDTO.builder()
				.id(sellId)
				.username(sellInfoRequest.getUsername())
				.productName(sellInfoRequest.getProductName())
				.price(sellInfoRequest.getPrice())
				.longitude(sellInfoRequest.getLongitude())
				.latitude(sellInfoRequest.getLatitude())
				.build();

		infoDTO = sellInfoService.update(infoDTO);

		SellInfoResponse infoResponse = SellInfoResponse.builder()
				.id(infoDTO.getId())
				.username(infoDTO.getUsername())
				.productName(infoDTO.getProductName())
				.price(infoDTO.getPrice())
				.createAt(infoDTO.getCreateAt())
				.updateAt(infoDTO.getUpdateAt())
				.longitude(infoDTO.getLongitude())
				.latitude(infoDTO.getLatitude())
				.sellState(infoDTO.getSellState())
				.build();

		if (sellImgRequest == null) {
			SellFeignResponse sellFeignResponse = 
					new SellFeignResponse(infoResponse, "there is no image");
			return ResponseEntity.ok().body(sellFeignResponse);
		}

		SellImgDTO imgDTO = SellImgDTO.builder()
				.sid(infoResponse.getId())
				.filename(sellImgRequest.getFilename())
				.imgData(sellImgRequest.getImgData())
				.thumData(sellImgRequest.getThumData())
				.build();
		String sid = sellImgService.updateImg(imgDTO);
		SellFeignResponse sellFeignResponse = 
				new SellFeignResponse(infoResponse,
				"image successfully uploaded with id: " + sid);
		return ResponseEntity.ok().body(sellFeignResponse);
	}
	
	
	// sell은 상태가 SOLD_OUT이 아닌 때에만 삭제 가능함
	@DeleteMapping("/board-sells/{sellId}")
	public ResponseEntity<String> delete(@PathVariable("sellId") String sellId) {

		boolean deleteSellImg = sellInfoService.delete(sellId);
		
		if(!deleteSellImg) {
			return ResponseEntity.ok().body("거래 정보는 삭제할 수 없었습니다");
		}
		
		sellImgService.deleteImg(sellId);
		
		return ResponseEntity.ok().body("거래 정보도 삭제되었습니다");
		
	}
	
	
	
	// sell 데이터만 직접적으로 생성할 일이 있을때 사용하는 엔드포인트.
	@PostMapping("/sells")
	public ResponseEntity<?> insert(@RequestHeader("Authorization") String bearerToken, MultipartHttpServletRequest request) {
		String token = bearerToken.replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());

		String username = Jwts.parser()
				.setSigningKey(encodedSecKey)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		
		MultipartFile imgFile = request.getFile("imgFile");
		
		String productName = request.getParameter("productName");
		Long price = Long.parseLong(request.getParameter("price"));
		Double longitude = Double.parseDouble(request.getParameter("longitude"));
		Double latitude = Double.parseDouble(request.getParameter("latitude"));
		
		byte[] imgData = ImageResize.getResizedImageData(imgFile, 500, 500, 0.75);
		byte[] thumData = ImageResize.getResizedImageData(imgFile, 50, 50, 0.5);
			
				
		SellInfoDTO infoDTO = SellInfoDTO.builder()
				.username(username)
				.productName(productName)
				.price(price)
				.longitude(longitude)
				.latitude(latitude)
				.build();
				
		infoDTO = sellInfoService.insert(infoDTO);
		
		SellInfoResponse infoResponse = SellInfoResponse.builder()
				.id(infoDTO.getId())
				.username(infoDTO.getUsername())
				.productName(infoDTO.getProductName())
				.price(infoDTO.getPrice())
				.createAt(infoDTO.getCreateAt())
				.updateAt(infoDTO.getUpdateAt())
				.longitude(infoDTO.getLongitude())
				.latitude(infoDTO.getLatitude())
				.sellState(infoDTO.getSellState())
				.build();
		
		SellImgDTO imgDTO = SellImgDTO.builder()
				.sid(infoResponse.getId())
				.imgData(imgData)
				.thumData(thumData)
				.build();
		
		String sid = sellImgService.insertImg(imgDTO);
		
		Map<String, Object> map = new HashMap<>();
		map.put("infoResult", infoResponse);
		map.put("imgSid", sid);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(map);
		
	}
	
	@PutMapping("/sells")
	public ResponseEntity<?> update(@RequestBody SellInfoRequest request) {
		
		SellInfoDTO dto = SellInfoDTO.builder()
				.id(request.getId())
				.username(request.getUsername())
				.buyer(request.getBuyer())
				.productName(request.getProductName())
				.price(request.getPrice())
				.longitude(request.getLongitude())
				.latitude(request.getLatitude())
				.sellState(request.getSellState())
				.build();
		
		dto = sellInfoService.update(dto);
		
		SellInfoResponse response = SellInfoResponse.builder()
				.id(dto.getId())
				.username(dto.getUsername())
				.buyer(dto.getBuyer())
				.productName(dto.getProductName())
				.price(dto.getPrice())
				.createAt(dto.getCreateAt())
				.updateAt(dto.getUpdateAt())
				.finishAt(dto.getFinishAt())
				.longitude(dto.getLongitude())
				.latitude(dto.getLatitude())
				.sellState(dto.getSellState())
				.build();
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
		
	}
	
	
	@GetMapping("/sells/findbyrange")
	public ResponseEntity<?> findAround(@RequestHeader(value = "Authorization", required = false) String bearerToken, @RequestParam("range") Double rangeInKm){
		
		if(bearerToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		String token = bearerToken.replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());

		Double latitude;
		try {
			latitude = (double)Jwts.parser()
					.setSigningKey(encodedSecKey)
					.parseClaimsJws(token)
					.getBody()
					.get("latitude");
		} 
		catch (ExpiredJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			
		} 
		catch (UnsupportedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (MalformedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (SignatureException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Double longitude;
		try {
			longitude = (double)Jwts.parser()
					.setSigningKey(encodedSecKey)
					.parseClaimsJws(token)
					.getBody()
					.get("longitude");
		} catch (ExpiredJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			
		} 
		catch (UnsupportedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (MalformedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (SignatureException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		List<SellInfoDTO> sellInfoDTOList = sellInfoService.findAroundAll(longitude, latitude, rangeInKm);
		
		return ResponseEntity.ok().body(sellInfoDTOList);
	}
	
	@GetMapping("/sells/findbyrange/page")
	public ResponseEntity<?> findAroundListPage(@RequestHeader(value = "Authorization", required = false) String bearerToken, @RequestParam("range") Double rangeInKm, @RequestParam("page") int page){
		
		if(bearerToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		}
		
		String token = bearerToken.replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());

		Double latitude;
		try {
			latitude = (double)Jwts.parser()
					.setSigningKey(encodedSecKey)
					.parseClaimsJws(token)
					.getBody()
					.get("latitude");
		} 
		catch (ExpiredJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			
		} 
		catch (UnsupportedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (MalformedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (SignatureException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Double longitude;
		try {
			longitude = (double)Jwts.parser()
					.setSigningKey(encodedSecKey)
					.parseClaimsJws(token)
					.getBody()
					.get("longitude");
		} catch (ExpiredJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			
		} 
		catch (UnsupportedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (MalformedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (SignatureException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		List<SellInfoDTO> sellInfoDTOList = sellInfoService.findAroundAll(longitude, latitude, rangeInKm);
		List<SellInfoDTO> sellInfoDTOListPage = new ArrayList<>();
		
		if(sellInfoDTOList.size() < page*10-10) {
			return ResponseEntity.ok().body(null);
		}
		
		if(sellInfoDTOList.size() < page*10) {
			sellInfoDTOListPage = sellInfoDTOList.subList(page*10 - 10, sellInfoDTOList.size());
		} else {
			sellInfoDTOListPage = sellInfoDTOList.subList(page*10 - 10, page*10);
		}
		return ResponseEntity.ok().body(sellInfoDTOListPage);
	}
	
	
	// review-service에서 리뷰 상태 변경 요청 하는 엔드포인트
	@PutMapping("/review-sells/isreviewed")
	public ResponseEntity<?> updateIsReviewed(@RequestBody SellInfoRequest sellInfoRequest){
		
		String id = sellInfoRequest.getId();
		String reviewer = sellInfoRequest.getBuyer();
		SellInfoDTO orgDTO = sellInfoService.findBySellId(id);
		
		
		if(orgDTO.getBuyer() == null || !orgDTO.getBuyer().equals(reviewer) ||
				orgDTO.getSellState() != SellState.SOLD_OUT) {
			
			return ResponseEntity.badRequest().body("failed");
		}
		
		SellInfoDTO sellInfoDTO = sellInfoService.isReviewed(id, sellInfoRequest.isReviewed());
		SellInfoResponse sellInfoResponse = SellInfoResponse.builder()
				.id(sellInfoDTO.getId())
				.username(sellInfoDTO.getUsername())
				.createAt(sellInfoDTO.getCreateAt())
				.buyer(sellInfoDTO.getBuyer())
				.productName(sellInfoDTO.getProductName())
				.price(sellInfoDTO.getPrice())
				.updateAt(sellInfoDTO.getUpdateAt())
				.finishAt(sellInfoDTO.getUpdateAt())
				.longitude(sellInfoDTO.getLongitude())
				.latitude(sellInfoDTO.getLatitude())
				.isReviewed(sellInfoDTO.isReviewed())
				.build();
		
		return ResponseEntity.ok().body(sellInfoResponse);
	}
	
	
	@PutMapping("/sells/reserve")
	public ResponseEntity<?> makeReservation(
			@RequestHeader(value = "Authorization", required = false) String bearerToken,
			@RequestBody SellInfoRequest sellInfoRequest) {
		
		if (bearerToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String token = bearerToken.replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());

		String username;
		try {
			username = Jwts.parser()
					.setSigningKey(encodedSecKey)
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
		} 
		catch (ExpiredJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (UnsupportedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (MalformedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (SignatureException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		String id = sellInfoRequest.getId();
		String buyer = sellInfoRequest.getBuyer();
		SellInfoDTO orgDTO = sellInfoService.findBySellId(id);

		if (!username.equals(orgDTO.getUsername())) {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		if (orgDTO.getSellState() == SellState.SOLD_OUT || orgDTO.getSellState() == SellState.RESERVED) {

			return ResponseEntity.badRequest().body("failed");
		}
		
		SellInfoDTO sellInfoDTO = sellInfoService.reserve(id, buyer);

		SellInfoResponse sellInfoResponse = SellInfoResponse.builder()
				.id(sellInfoDTO.getId())
				.username(sellInfoDTO.getUsername())
				.createAt(sellInfoDTO.getCreateAt())
				.sellState(sellInfoDTO.getSellState())
				.buyer(sellInfoDTO.getBuyer())
				.productName(sellInfoDTO.getProductName())
				.price(sellInfoDTO.getPrice())
				.updateAt(sellInfoDTO.getUpdateAt())
				.finishAt(sellInfoDTO.getFinishAt())
				.longitude(sellInfoDTO.getLongitude())
				.latitude(sellInfoDTO.getLatitude())
				.isReviewed(sellInfoDTO.isReviewed())
				.build();
		return ResponseEntity.ok().body(sellInfoResponse);
	}
	
	
	@PutMapping("/sells/soldout")
	public ResponseEntity<?> makeSoldOut(
			@RequestHeader(value = "Authorization", required = false) String bearerToken,
			@RequestBody SellInfoRequest sellInfoRequest) {
		
		if (bearerToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String token = bearerToken.replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());

		String username;
		try {
			username = Jwts.parser()
					.setSigningKey(encodedSecKey)
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
		} 
		catch (ExpiredJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (UnsupportedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (MalformedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (SignatureException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		String id = sellInfoRequest.getId();
		String buyer = sellInfoRequest.getBuyer();
		SellInfoDTO orgDTO = sellInfoService.findBySellId(id);
		
		if (!username.equals(orgDTO.getUsername())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (orgDTO.getSellState() == SellState.SOLD_OUT) {

			return ResponseEntity.badRequest().body("failed");
		}
		SellInfoDTO sellInfoDTO = sellInfoService.soldout(id, buyer);

		SellInfoResponse sellInfoResponse = SellInfoResponse.builder()
				.id(sellInfoDTO.getId())
				.username(sellInfoDTO.getUsername())
				.createAt(sellInfoDTO.getCreateAt())
				.sellState(sellInfoDTO.getSellState())
				.buyer(sellInfoDTO.getBuyer())
				.productName(sellInfoDTO.getProductName())
				.price(sellInfoDTO.getPrice())
				.updateAt(sellInfoDTO.getUpdateAt())
				.finishAt(sellInfoDTO.getFinishAt())
				.longitude(sellInfoDTO.getLongitude())
				.latitude(sellInfoDTO.getLatitude())
				.isReviewed(sellInfoDTO.isReviewed())
				.build();
		
		return ResponseEntity.ok().body(sellInfoResponse);
	}
	
	
	@PutMapping("/sells/onsale")
	public ResponseEntity<?> makeOnSale(
			@RequestHeader(value = "Authorization", required = false) String bearerToken,
			@RequestBody SellInfoRequest sellInfoRequest) {
		
		if (bearerToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		}

		String token = bearerToken.replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());

		String username;

		try {
			username = Jwts.parser()
					.setSigningKey(encodedSecKey)
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
		} 
		catch (ExpiredJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (UnsupportedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (MalformedJwtException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (SignatureException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		String id = sellInfoRequest.getId();
		SellInfoDTO orgDTO = sellInfoService.findBySellId(id);
		
		if (!username.equals(orgDTO.getUsername())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (orgDTO.getSellState() == SellState.ON_SALE) {

			return ResponseEntity.badRequest().body("already onsale");
		}
		
		SellInfoDTO sellInfoDTO = sellInfoService.onsale(id);

		SellInfoResponse sellInfoResponse = SellInfoResponse.builder()
				.id(sellInfoDTO.getId())
				.username(sellInfoDTO.getUsername())
				.createAt(sellInfoDTO.getCreateAt())
				.sellState(sellInfoDTO.getSellState())
				.buyer(sellInfoDTO.getBuyer())
				.productName(sellInfoDTO.getProductName())
				.price(sellInfoDTO.getPrice())
				.updateAt(sellInfoDTO.getUpdateAt())
				.finishAt(sellInfoDTO.getFinishAt())
				.longitude(sellInfoDTO.getLongitude())
				.latitude(sellInfoDTO.getLatitude())
				.isReviewed(sellInfoDTO.isReviewed())
				.build();
		
		return ResponseEntity.ok().body(sellInfoResponse);
	}

	/* for flutter mobile */
	@GetMapping("/sells/m/findbyrange/page")
	public ResponseEntity<?> mfindAroundListPage(@RequestParam("range") Double rangeInKm, 
			@RequestParam("longitude") Double longitude, 
			@RequestParam("latitude") Double latitude, 
			@RequestParam("page") int page){
		
		List<SellInfoDTO> sellInfoDTOList = sellInfoService.findAroundAll(longitude, latitude, rangeInKm);
		List<SellInfoDTO> sellInfoDTOListPage = new ArrayList<>();
		
		if(sellInfoDTOList.size() < page*10-10) {
			return ResponseEntity.ok().body(new ArrayList<>());
		}
		
		if(sellInfoDTOList.size() < page*10) {
			sellInfoDTOListPage = sellInfoDTOList.subList(page*10 - 10, sellInfoDTOList.size());
		} else {
			sellInfoDTOListPage = sellInfoDTOList.subList(page*10 - 10, page*10);
		}
		return ResponseEntity.ok().body(sellInfoDTOListPage);
	}
}
