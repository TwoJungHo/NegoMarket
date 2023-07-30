package kr.co.tj.imgfileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class ImgfileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImgfileServiceApplication.class, args);
	}

}
