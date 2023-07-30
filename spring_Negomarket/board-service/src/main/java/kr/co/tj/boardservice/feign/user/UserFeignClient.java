package kr.co.tj.boardservice.feign.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@FeignClient(name = "user-service")
public interface UserFeignClient {
	
	@PostMapping("/user-service/pwvalidation")
	public ResponseEntity<Boolean> pwValidation(@RequestBody UserFeignLoginRequest userFeignLoginRequest);
	
	
}
