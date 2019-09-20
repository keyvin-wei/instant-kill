package com.keyvin.instantkill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author weiwh
 * @date 2019/8/11 10:58
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.keyvin"})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
