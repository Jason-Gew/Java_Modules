package gew.webview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application.message")
public class AppMessageConfig {

    private String welcome;
    private String keyError;
    private String duplicateUsername;
    private String duplicateEmail;

}
