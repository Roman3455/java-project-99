package hexlet.code.config;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatafakerConfig {

    @Bean
    Faker getFaker() {

        return new Faker();
    }
}
