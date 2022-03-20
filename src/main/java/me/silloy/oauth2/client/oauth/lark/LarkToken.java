package me.silloy.oauth2.client.oauth.lark;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LarkToken extends OAuth2AccessToken {

  private String openId;
  private String unionId;
  private String userId;

  public LarkToken(String accessToken, String tokenType, Integer expiresIn,
      String refreshToken, String scope, String rawResponse,
      String openId, String unionId, String userId) {
    super(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse);
    this.openId = openId;
    this.unionId = unionId;
    this.userId = userId;
  }
}
