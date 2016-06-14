package com.ppi.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration;

/**
 * Application
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@SpringBootApplication(exclude = HazelcastJpaDependencyAutoConfiguration.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
