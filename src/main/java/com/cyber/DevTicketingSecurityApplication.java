package com.cyber;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DevTicketingSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevTicketingSecurityApplication.class, args);
    }

    //third party beans need to be inside @Configuration -- @SpringBootApplication covers @Configuration !!
    @Bean
    public ModelMapper mapper(){
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
