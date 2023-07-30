package kr.co.tj.apigatewayservice.filter;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;

import reactor.core.publisher.Mono;

@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config>{
	
	//private static final String SECRET_KEY="tj705team";
	private Environment env;
	
	@Autowired
	public AuthorizationFilter(Environment env) {
		super(Config.class);
		this.env = env;
	}

	//@Data
	public static class Config{
//		private Integer num1;
//		private Integer num2;
		
	}


	@Override
	public GatewayFilter apply(Config config) {
		
//		config.num1 이런식으로 사용 가능
		
		
		return (exchange, chain) -> {
			//반드시 reacitve 붙은 패키지명 확인하고 넣자.
			ServerHttpRequest request = exchange.getRequest();
			
			if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "authorization 키가 없습니다.", HttpStatus.UNAUTHORIZED);
			}
			
			String bearerToken = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
		
			String token = bearerToken.replace("Bearer ", "");
			
			
			
			if(!isJwtValid(token)) {
			
				
				return onError(exchange, "토큰이 유효하지 않습니다..", HttpStatus.UNAUTHORIZED);
				
			}
			
			return chain.filter(exchange);
		};
	}
	
	private boolean isJwtValid(String token) {
		boolean isValid = true;
		String subject = null;
		
		String str = env.getProperty("data.SECRET_KEY");
		String encodedStr = Base64.getEncoder().encodeToString(str.getBytes());
		
		try {
			subject = Jwts.parser().setSigningKey(encodedStr)
			.parseClaimsJws(token).getBody().getSubject();
			
		} catch (Exception e) {
			e.printStackTrace();
			isValid = false;
		}
		
		if(subject == null || subject.isEmpty()) {
			isValid = false;
		}
		
		return isValid;
	}


	private Mono<Void> onError(ServerWebExchange exchange, String string, HttpStatus status) {
		ServerHttpResponse response =  exchange.getResponse();
		response.setStatusCode(status);
		
		
		return response.setComplete();
	}
	
	

}
