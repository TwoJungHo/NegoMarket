package kr.co.tj.userservice.sec;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter{
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
		.cors()
		.and()
		.csrf()
		.disable()
		.httpBasic()
		.disable()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.authorizeRequests()
		.antMatchers("*").permitAll()
		.antMatchers("/user-service/users/**").permitAll();
		
		http
		.headers().frameOptions().disable();
		
	}

}
