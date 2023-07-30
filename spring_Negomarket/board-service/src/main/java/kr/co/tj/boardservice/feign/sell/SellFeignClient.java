package kr.co.tj.boardservice.feign.sell;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;






@FeignClient(name = "sell-service")
public interface SellFeignClient {
	
	
	
	@PostMapping("/sell-service/board-sells")
	public ResponseEntity<SellFeignResponse> insert(@RequestBody SellFeignRequest sellFeignRequest);

	@PutMapping("/sell-service/board-sells/{sellId}")
	public ResponseEntity<SellFeignResponse> update(@PathVariable String sellId, @RequestBody SellFeignRequest sellFeignRequest);
	
	@GetMapping("/sell-service/board-sells/{sellId}")
	public ResponseEntity<SellFeignGetResponse> getSell(@PathVariable String sellId);
	
	@DeleteMapping("/sell-service/board-sells/{sellId}")
	public ResponseEntity<String> delete(@PathVariable("sellId") String sellId);

}
