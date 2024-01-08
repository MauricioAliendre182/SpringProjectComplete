package com.mapscode.orders_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    // We need to add the @LoadBalanced annotation to enable the automatic load balance
    // at the moment to make an HTTP request
    @LoadBalanced
    public WebClient.Builder webClient() {
        // Whole services are working except for this endpoint of post an order
        // THis is because we are not adding the token bear here
        // To add the bearer token we will add .filter(new ServletBearerExchangeFilterFunction())
        return WebClient.builder().filter(new ServletBearerExchangeFilterFunction());
    }
}
