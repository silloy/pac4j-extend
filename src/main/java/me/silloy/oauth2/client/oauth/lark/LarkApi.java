package me.silloy.oauth2.client.oauth.lark;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import java.io.OutputStream;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LarkApi extends DefaultApi20 {

  public static final String APPID = "app_id";

  @Getter
  private final String authServer = "https://open.feishu.cn";


  @Override
  public String getAccessTokenEndpoint() {
    return authServer + "/open-apis/authen/v1/access_token";
  }

  @Override
  protected String getAuthorizationBaseUrl() {
    return authServer + "/open-apis/authen/v1/index";
  }

  @Override
  public String getRefreshTokenEndpoint() {
    return authServer + "/open-apis/authen/v1/refresh_access_token";
  }

  @Override
  public String getAuthorizationUrl(String responseType, String apiKey, String callback, String scope, String state,
      Map<String, String> additionalParams) {
    String authorizationUrl = super.getAuthorizationUrl(responseType, apiKey, callback, scope, state, additionalParams);
    authorizationUrl = authorizationUrl.replace(OAuthConstants.CLIENT_ID, APPID);
    return authorizationUrl;
  }

  @Override
  public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
    return LarkJsonExtractor.instance();
  }


  @Override
  public OAuth20Service createService(String apiKey, String apiSecret, String callback, String defaultScope,
      String responseType, OutputStream debugStream, String userAgent,
      HttpClientConfig httpClientConfig, HttpClient httpClient) {
    return new LarkService(this, apiKey, apiSecret,
        callback, defaultScope, responseType,
        userAgent, httpClientConfig, httpClient);
  }

}
