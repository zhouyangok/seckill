package cn.crazyang.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
@MapperScan("cn.crazyang.seckill.dao")
public class SeckillApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SeckillApplication.class, args);
	}


}
