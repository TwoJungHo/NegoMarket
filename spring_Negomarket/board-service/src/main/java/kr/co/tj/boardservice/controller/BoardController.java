package kr.co.tj.boardservice.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.jsonwebtoken.Jwts;
import kr.co.tj.boardservice.dto.BoardDTO;
import kr.co.tj.boardservice.dto.BoardRequest;
import kr.co.tj.boardservice.dto.BoardResponse;
import kr.co.tj.boardservice.feign.sell.SellFeignClient;
import kr.co.tj.boardservice.feign.sell.SellFeignGetResponse;
import kr.co.tj.boardservice.feign.sell.SellFeignRequest;
import kr.co.tj.boardservice.feign.sell.SellFeignResponse;
import kr.co.tj.boardservice.feign.sell.SellImgRequest;
import kr.co.tj.boardservice.feign.sell.SellInfoRequest;
import kr.co.tj.boardservice.feign.sell.SellInfoResponse;
import kr.co.tj.boardservice.feign.sell.SellState;
import kr.co.tj.boardservice.feign.user.UserFeignClient;
import kr.co.tj.boardservice.feign.user.UserFeignLoginRequest;
import kr.co.tj.boardservice.service.BoardService;
import kr.co.tj.boardservice.utils.ImageResize;
import net.coobird.thumbnailator.Thumbnails;


@RestController
@RequestMapping("/board-service")
public class BoardController {

	private Environment env;
	private BoardService boardService;
	private SellFeignClient sellFeignClient;
	private UserFeignClient userFeignClient;
		
	@Autowired
	public BoardController(Environment env, BoardService boardService, SellFeignClient sellFeignClient, UserFeignClient userFeignClient) {
		super();
		this.env = env;
		this.boardService = boardService;
		this.sellFeignClient = sellFeignClient;
		this.userFeignClient = userFeignClient;

	}
	
	@GetMapping("/boards/{sellId}")
	public ResponseEntity<?> getBoard(@PathVariable("sellId") String sellId){
		ResponseEntity<SellFeignGetResponse> feignResponse = sellFeignClient.getSell(sellId);
		SellFeignGetResponse sellFeignGetResponse = feignResponse.getBody();
		SellInfoResponse sellInfoResponse = sellFeignGetResponse.getSellInfoResponse();				
		BoardDTO boardDTO = boardService.findBySellId(sellId);
		
		BoardResponse boardResponse = BoardResponse.builder()
				.id(boardDTO.getId())
				.sellId(boardDTO.getSellId())
				.username(boardDTO.getUsername())
				.title(boardDTO.getTitle())
				.deltaString(boardDTO.getDeltaString())
				.htmlString(boardDTO.getHtmlString())
				.sellInfoResponse(sellInfoResponse)
				.build();
				
		return ResponseEntity.ok().body(boardResponse);
	}

	//게시글 입력, sell-service에 sell 데이터를 전송하고 기록함.
	@PostMapping("/boards")
	public ResponseEntity<?> insert(@RequestHeader("Authorization") String bearerToken, MultipartHttpServletRequest request){
		String token = bearerToken.replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());

		String username = Jwts.parser()
				.setSigningKey(encodedSecKey)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		
		SellImgRequest sellImgRequest = null;
		
		if(request.getFile("imgFile") != null) {
			
			MultipartFile imgFile = request.getFile("imgFile");
			String imgFilename = imgFile.getOriginalFilename();
			System.out.println(imgFilename);
			
			byte[] imgData = ImageResize.getResizedImageData(imgFile, 500, 500, 0.75);
			byte[] thumData = ImageResize.getResizedImageData(imgFile, 50, 50, 0.5);
			
			sellImgRequest = SellImgRequest.builder()
					.sid(null)
					.filename(imgFilename)
					.imgData(imgData)
					.thumData(thumData)
					.build();
		} 
		
		String productName = request.getParameter("productName");
		Long price = Long.parseLong(request.getParameter("price"));
		Double longitude = Double.parseDouble(request.getParameter("longitude"));
		Double latitude = Double.parseDouble(request.getParameter("latitude"));
	
		SellInfoRequest sellInfoRequest = SellInfoRequest.builder()
				.id(null)
				.username(username)
				.buyer(null)
				.productName(productName)
				.price(price)
				.longitude(longitude)
				.latitude(latitude)
				.isReviewed(false)
				.build();
		
		SellFeignRequest sellFeignRequest = SellFeignRequest.builder()
				.sellImgRequest(sellImgRequest)
				.sellInfoRequest(sellInfoRequest)
				.build();
		
		ResponseEntity<SellFeignResponse> feignResponse = sellFeignClient.insert(sellFeignRequest);
		
		if(feignResponse.getStatusCode() != HttpStatus.OK) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		SellFeignResponse sellFeignResponse = feignResponse.getBody();
		SellInfoResponse sellInfoResponse = sellFeignResponse.getSellInfoResponse();
		
		String sellId = sellInfoResponse.getId();
		String title = request.getParameter("title");
		String deltaString = request.getParameter("deltaString");
		String htmlString = request.getParameter("htmlString");
		
		BoardRequest boardRequest = BoardRequest.builder()
				.sellId(sellId)
				.username(username)
				.title(title)
				.deltaString(deltaString)
				.htmlString(htmlString)
				.build();
		
		BoardDTO boardDTO = BoardDTO.builder()
				.sellId(boardRequest.getSellId())
				.username(boardRequest.getUsername())
				.title(boardRequest.getTitle())
				.deltaString(boardRequest.getDeltaString())
				.htmlString(boardRequest.getHtmlString())
				.build();
		
		boardDTO = boardService.insert(boardDTO);
		
		BoardResponse boardResponse = BoardResponse.builder()
				.id(boardDTO.getId())
				.sellId(boardDTO.getSellId())
				.title(boardDTO.getTitle())
				.deltaString(boardDTO.getDeltaString())
				.htmlString(boardDTO.getHtmlString())
				.build();
		
		return ResponseEntity.ok().body(boardResponse);
	}
	
	
	@PutMapping("/boards/{sellId}")
	public ResponseEntity<?> update(@PathVariable("sellId") String sellId, @RequestHeader("Authorization") String bearerToken, 
			MultipartHttpServletRequest request){
		
		String token = bearerToken.replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());

		String username = Jwts.parser()
				.setSigningKey(encodedSecKey)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		
		SellImgRequest sellImgRequest = null;
		
		if(request.getFile("imgFile") != null) {
			
			MultipartFile imgFile = request.getFile("imgFile");
			String imgFilename = imgFile.getOriginalFilename();
			System.out.println(imgFilename);
			
			byte[] imgData = ImageResize.getResizedImageData(imgFile, 500, 500, 0.75);
			byte[] thumData = ImageResize.getResizedImageData(imgFile, 50, 50, 0.5);
			
			sellImgRequest = SellImgRequest.builder()
					.sid(sellId)
					.filename(imgFilename)
					.imgData(imgData)
					.thumData(thumData)
					.build();
		} 
		
		String productName = request.getParameter("productName");
		Long price = Long.parseLong(request.getParameter("price"));
		Double longitude = Double.parseDouble(request.getParameter("longitude"));
		Double latitude = Double.parseDouble(request.getParameter("latitude"));
				
		SellInfoRequest sellInfoRequest = SellInfoRequest.builder()
				.id(sellId)
				.username(username)
				.buyer(null)
				.productName(productName)
				.price(price)
				.longitude(longitude)
				.latitude(latitude)
				.isReviewed(false)
				.build();
		
		SellFeignRequest sellFeignRequest = SellFeignRequest.builder()
				.sellImgRequest(sellImgRequest)
				.sellInfoRequest(sellInfoRequest)
				.build();
		
		ResponseEntity<SellFeignResponse> feignResponse = sellFeignClient.update(sellId, sellFeignRequest);
		
		if(feignResponse.getStatusCode() != HttpStatus.OK) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		
		SellFeignResponse sellFeignResponse = feignResponse.getBody();
		SellInfoResponse sellInfoResponse = sellFeignResponse.getSellInfoResponse();
				
		String title = request.getParameter("title");
		String deltaString = request.getParameter("deltaString");
		String htmlString = request.getParameter("htmlString");
		
		BoardRequest boardRequest = BoardRequest.builder()
				.sellId(sellId)
				.username(username)
				.title(title)
				.deltaString(deltaString)
				.htmlString(htmlString)
				.build();
		
		BoardDTO boardDTO = BoardDTO.builder()
				.sellId(boardRequest.getSellId())
				.username(boardRequest.getUsername())
				.title(boardRequest.getTitle())
				.deltaString(boardRequest.getDeltaString())
				.htmlString(boardRequest.getHtmlString())
				.build();
		
		boardDTO = boardService.update(boardDTO);
		
		BoardResponse boardResponse = BoardResponse.builder()
				.id(boardDTO.getId())
				.sellId(boardDTO.getSellId())
				.title(boardDTO.getTitle())
				.deltaString(boardDTO.getDeltaString())
				.htmlString(boardDTO.getHtmlString())
				.build();
	
		return ResponseEntity.ok().body(boardResponse);
	}
	
	
	@DeleteMapping("/boards/{sellId}")
	public ResponseEntity<String> delete(@RequestHeader("Authorization") String bearerToken, @PathVariable("sellId") String sellId, @RequestBody String password){
		
		String token = bearerToken.replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());

		String username = Jwts.parser()
				.setSigningKey(encodedSecKey)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		
		String sellDelResponse = sellFeignClient.delete(sellId).getBody();
		
		BoardDTO boardDTO = boardService.findBySellId(sellId);
		
		if(!boardDTO.getUsername().equals(username)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("삭제 권한 없음");
		}
		
		UserFeignLoginRequest userFeignLoginRequest = UserFeignLoginRequest.builder()
				.username(username)
				.password(password)
				.build();
		
		ResponseEntity<Boolean> response = userFeignClient.pwValidation(userFeignLoginRequest);
		
		boolean isPwValid = response.getBody();
			
		if(!isPwValid) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호를 확인하세요");
		}
		
		boardService.delete(sellId);
		
		return ResponseEntity.ok().body(sellDelResponse);
	}
	

	@GetMapping("/testinsert")
	public ResponseEntity<?> testinsert(int trialNum) {
		
		InputStream fileStream = null;
		ByteArrayOutputStream outputStream = null;
		for (int i = 0; i < trialNum; i++) {
			
			int testImgNum = i%10;
			String testImgFileName = "/testimg/testimg" + testImgNum + ".jpg";
			
			byte[] imageData = null;
			byte[] thumData = null;
			
			try {
				fileStream = getClass().getResourceAsStream(testImgFileName);
				outputStream = new ByteArrayOutputStream();
				
				Thumbnails.of(fileStream)
				.size(500, 500)
				.outputQuality(0.8)
				.toOutputStream(outputStream);
				imageData = outputStream.toByteArray();
				
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				if(fileStream != null) {
					try {
						fileStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			try {
				fileStream = getClass().getResourceAsStream(testImgFileName);
				outputStream = new ByteArrayOutputStream();
				
				Thumbnails.of(fileStream)
				.size(50, 50)
				.outputQuality(0.5)
				.toOutputStream(outputStream);
				thumData = outputStream.toByteArray();
				
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				if(fileStream != null) {
					try {
						fileStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			SellImgRequest sellImgRequest = SellImgRequest.builder()
					.id(null)
					.filename("testimg (" + testImgNum + ").jpg")
					.imgData(imageData)
					.thumData(thumData)
					.build();
			
			
			//신촌역 좌표
		    double baseLatitude = 37.5552314845079;  // 기준 위도
		    double baseLongitude = 126.93693998959822;  // 기준 경도
		    
		    // 대략적으로 일정범위(사각형 비슷하게) 좌표값 생성, 할당
		    double latitude = (baseLatitude - 0.5) + Math.random() * 1.0;
		    double longitude = (baseLongitude - 0.4) + Math.random() * 0.8;
		     
		    String sti = String.format("%04d", i);
		    int caseNum = i%5;
		
		    String username = "jwy" + caseNum;
		    String buyer = "buy" + caseNum;
			
			String productName = null;
			Long price = null;
			
			//5가지 상품 종류를 마련했습니다!!
			switch (caseNum) {
			case 0:
				productName = "애플 맥 장원영" + sti;
				price = 3000000L + i;
				break;
			case 1:
				productName = "나이키 장원영" + sti;
				price = 120000L + i;
				break;
			case 2:
				productName = "샤넬 장원영" + sti;
				price = 5000L - i;
				break;
			case 3:
				productName = "프라다 장원영" + sti;
				price = 1000000L + i;
				break;
			case 4:
				productName = "롤렉스 장원영" + sti;
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
			
			// enum 설정
			SellState sellState = SellState.ON_SALE;
			
			SellInfoRequest sellInfoRequest = SellInfoRequest.builder()
					.id(null)
					.username(username)
					.buyer(buyer)
					.productName(productName)
					.price(price)
					.longitude(longitude)
					.latitude(latitude)
					.sellState(sellState)
					.isReviewed(false)
					.build();
			
			SellFeignRequest sellFeignRequest = SellFeignRequest.builder()
					.sellImgRequest(sellImgRequest)
					.sellInfoRequest(sellInfoRequest)
					.build();
			
			ResponseEntity<SellFeignResponse> feignResponse = sellFeignClient.insert(sellFeignRequest);
			
			if(feignResponse.getStatusCode() != HttpStatus.OK) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			
			SellFeignResponse sellFeignResponse = feignResponse.getBody();
			SellInfoResponse sellInfoResponse = sellFeignResponse.getSellInfoResponse();
			
			String sellId = sellInfoResponse.getId();
			String title = productName;
			String deltaString = "[{\"insert\":\"" + productName + "\\n\"}]";
			String htmlString = "<p>" + productName + "</p>";
			
			BoardRequest boardRequest = BoardRequest.builder()
					.sellId(sellId)
					.username(username)
					.title(title)
					.deltaString(deltaString)
					.htmlString(htmlString)
					.build();
			
			BoardDTO boardDTO = BoardDTO.builder()
					.sellId(boardRequest.getSellId())
					.username(boardRequest.getUsername())
					.title(boardRequest.getTitle())
					.deltaString(boardRequest.getDeltaString())
					.htmlString(boardRequest.getHtmlString())
					.build();
			
			boardDTO = boardService.insert(boardDTO);
		} // 여기가 for 문의 끝
		return ResponseEntity.ok().build();
	}
}
