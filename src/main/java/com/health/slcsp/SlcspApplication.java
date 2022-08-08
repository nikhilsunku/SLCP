package com.health.slcsp;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.health.slcsp.util.SlcspDataUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class SlcspApplication extends SpringBootServletInitializer {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SlcspApplication.class, args);
		try {
			context.getBean(SlcspDataUtil.class).readAndUpdatePlanData();
		} catch (Exception e) {
			log.error("Error in Zipcode and Plans data.");
			e.printStackTrace();
		}
	}

}
