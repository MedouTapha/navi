package com.navi.education;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NaviEducationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NaviEducationApplication.class, args);
    }
}
