package edu.example.testingsystem.actuator;

import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Для отслеживания запросов к сервису в конечной точке /management/httpexchanges актуатора
 */
@Configuration
public class HttpTraceConfig {

    // /management/httpexchanges
    @Bean
    public InMemoryHttpExchangeRepository createTraceRepository() {
        return new InMemoryHttpExchangeRepository();
    }

}
