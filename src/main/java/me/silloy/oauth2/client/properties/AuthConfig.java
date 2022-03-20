package me.silloy.oauth2.client.properties;

import me.silloy.oauth2.client.oauth.lark.LarkProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("pac4j")
public class AuthConfig {

  private LarkProperties lark;

}
