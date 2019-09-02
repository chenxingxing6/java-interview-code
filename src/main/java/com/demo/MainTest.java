package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * User: lanxinghua
 * Date: 2019/9/2 16:05
 * Desc:
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MainTest extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MainTest.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MainTest.class);
    }
}
