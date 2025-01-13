package edu.example.testingsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
@EnableScheduling
public class TestingSystemApplication implements WebMvcConfigurer {

    private final static Logger log = LoggerFactory.getLogger(TestingSystemApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TestingSystemApplication.class, args);
        log.info("Application started");
    }

    /**
     * Добавляет конечные точки без контроллеров
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/inactive");
        registry.addViewController("/login");
    }

}
