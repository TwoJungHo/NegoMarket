package kr.co.tj.userservice.sec;

import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kr.co.tj.userservice.info.persistence.UserInfoEntity;

@Component
public class TokenProvider {
	
	private Environment env;
	
	@Autowired
	public TokenProvider(Environment env) {
		super();
		this.env = env;
	}

	public String create(UserInfoEntity userInfoEntity) {
		long now = System.currentTimeMillis();
		
		Date today = new Date(now);
		Date expireDate = new Date(now + 1000*60*60*24*365);
		String str = env.getProperty("data.SECRET_KEY");
		String encodedStr = Base64.getEncoder().encodeToString(str.getBytes());
		
		return Jwts.builder()
				.signWith(SignatureAlgorithm.HS512, encodedStr)
				.setSubject(userInfoEntity.getUsername())
				.setIssuer("user-service")
				.setIssuedAt(today)
				.setExpiration(expireDate)
				.claim("longitude", userInfoEntity.getLongitude())
				.claim("latitude", userInfoEntity.getLatitude())
				.compact();
	}

}