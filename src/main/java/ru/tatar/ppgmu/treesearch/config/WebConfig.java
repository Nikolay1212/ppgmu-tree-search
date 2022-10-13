package ru.tatar.ppgmu.treesearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@RefreshScope
public class WebConfig {

    @Value("${loadbalanced.connect.timeout:500}")
    private int connectTimeout;
    @Value("${loadbalanced.read.timeout:10000}")
    private int readTimeout;

    @RefreshScope
    @Bean
    @LoadBalanced
    public RestTemplate loadBalanced(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.of(connectTimeout, ChronoUnit.MILLIS))
                .setReadTimeout(Duration.of(readTimeout, ChronoUnit.MILLIS))
                .build();
        return restTemplate;
    }
}