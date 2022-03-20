package me.silloy.oauth2.client.oauth.lark;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.silloy.oauth2.client.util.JsonUtil;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.SneakyThrows;

public class LarkService extends OAuth20Service {


  private final DefaultApi20 api;

  private final String apiKey;
  private final String apiSecret;


  public LarkService(DefaultApi20 api,
      String apiKey,
      String apiSecret,
      String callback,
      String defaultScope,
      String responseType,
      String userAgent,
      HttpClientConfig httpClientConfig,
      HttpClient httpClient) {
    super(api, apiKey, apiSecret, callback, defaultScope, responseType, System.out, userAgent, httpClientConfig, httpClient);
    this.api = api;
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
  }


  @Override
  protected OAuthRequest createAccessTokenRequest(AccessTokenRequestParams params) {
    final OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());

    final Map<String, String> map = new HashMap<>();
    map.put(OAuthConstants.GRANT_TYPE, OAuthConstants.AUTHORIZATION_CODE);
    map.put(OAuthConstants.CODE, params.getCode());
    final String json = JsonUtil.toJson(map);

    request.setPayload(json);
    request.addHeader("Content-Type", "application/json; charset=utf-8");
    request.addHeader("Authorization", "Bearer " + appAccessToken(LarkAccessToken.APP));

    logRequestWithParams("access token", request);
    return request;
  }


  @SneakyThrows
  private String appAccessToken(LarkAccessToken tokenType) {
    Response response = execute(apiTokenRequest(tokenType));

    String body = response.getBody();
    PrimaryAccessToken token = new ObjectMapper().readValue(body, PrimaryAccessToken.class);
    return token.getAccessToken();
  }


  @Data
  public static class PrimaryAccessToken {

    private Integer code;
    private String msg;

    @JsonAlias({"tenant_access_token", "app_access_token"})
    private String accessToken;
    private Integer expire;
  }


  protected OAuthRequest apiTokenRequest(LarkAccessToken tokenType) {
    String url = String.format("https://open.feishu.cn/open-apis/auth/v3/%s_access_token/internal", tokenType.name().toLowerCase());
    final OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), url);
    Map<String, Object> map = Map.of("app_id", apiKey, "app_secret", apiSecret);

    final String json = JsonUtil.toJson(map);
    request.setPayload(json);
    request.addHeader("Content-Type", "application/json; charset=utf-8");

    return request;
  }

}
