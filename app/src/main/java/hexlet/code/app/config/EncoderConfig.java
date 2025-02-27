package hexlet.code.app.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import hexlet.code.app.component.RsaKeyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@RequiredArgsConstructor
public class EncoderConfig {

    private final RsaKeyProperties rsaKeys;

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
    PasswordEncoder passwordEncoder() {

        return new SCryptPasswordEncoder(cpuCost, memoryCost, parallelism, keyLength, saltLength);
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.getPublicKey()).privateKey(rsaKeys.getPrivateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    JwtDecoder jwtDecoder() {

        return NimbusJwtDecoder.withPublicKey(rsaKeys.getPublicKey()).build();
    }
}
