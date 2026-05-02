package com.certificaciones.backend.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(OperatorUserRepository repo, PasswordEncoder encoder) {
        return args -> {

            if (repo.findByUsername("admin").isEmpty()) {
                OperatorUser user = new OperatorUser();
                user.setUsername("admin");
                user.setPassword(encoder.encode("admin123"));

                repo.save(user);
            }
        };
    }
}
