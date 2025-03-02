package hexlet.code.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);

        //todo тесты для лейблов и тасков и фильтрации
        //todo уточнить по сваггеру п.8
        //todo уточнить по п.10
    }
}
