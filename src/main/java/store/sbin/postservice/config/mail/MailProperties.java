package store.sbin.postservice.config.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("spring.mail")
@Getter
@Setter
public class MailProperties {
    private String host;
    private Integer port;
    private String username;
    private String password;
}
