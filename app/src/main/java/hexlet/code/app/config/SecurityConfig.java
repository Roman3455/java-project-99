package hexlet.code.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

@Configuration
public class SecurityConfig {

    @Value("${security.scrypt.cpu-cost}")
    private int cpuCost;

    @Value("${security.scrypt.memory-cost}")
    private int memoryCost;

    @Value("${security.scrypt.parallelism}")
    private int parallelism;

    @Value("${security.scrypt.key-length}")
    private int keyLength;

    @Value("${security.scrypt.salt-length}")
    private int saltLength;

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new SCryptPasswordEncoder(cpuCost, memoryCost, parallelism, keyLength, saltLength);
    }
}
