package com.bowen.BZ_takeout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan
public class BZTakeoutApplication {
    public static void main(String[] args) {
        SpringApplication.run(BZTakeoutApplication.class, args);
        log.info("project start successful!...");
    }



}
