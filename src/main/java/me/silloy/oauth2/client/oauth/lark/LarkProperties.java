package me.silloy.oauth2.client.oauth.lark;

import lombok.Data;

@Data
public class LarkProperties {

  private String key;
  private String secret;
  private String callbackUrl;
}
