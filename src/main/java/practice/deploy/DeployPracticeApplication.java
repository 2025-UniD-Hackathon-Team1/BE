package practice.deploy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DeployPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeployPracticeApplication.class, args);
	}

}
