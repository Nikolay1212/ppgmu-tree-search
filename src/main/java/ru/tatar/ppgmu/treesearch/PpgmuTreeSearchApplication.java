package ru.tatar.ppgmu.treesearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@Configuration
@EnableScheduling
@SpringBootApplication
public class PpgmuTreeSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(PpgmuTreeSearchApplication.class, args);
    }

}


