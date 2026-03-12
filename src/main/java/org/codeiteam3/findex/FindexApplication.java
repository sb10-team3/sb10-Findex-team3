package org.codeiteam3.findex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FindexApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindexApplication.class, args);
    }

}
