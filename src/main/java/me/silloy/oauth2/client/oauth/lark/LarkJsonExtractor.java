package me.silloy.oauth2.client.oauth.lark;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.utils.Preconditions;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LarkJsonExtractor extends OAuth2AccessTokenJsonExtractor {

  protected LarkJsonExtractor() {
  }

  private static class InstanceHolder {

    private static final LarkJsonExtractor INSTANCE = new LarkJsonExtractor();
  }

  public static LarkJsonExtractor instance() {
    return LarkJsonExtractor.InstanceHolder.INSTANCE;
  }

  @Override
  public OAuth2AccessToken extract(Response response) throws IOException {
    final String body = response.getBody();
    Preconditions.checkEmptyString(body, "Response body is incorrect. Can't extract a token from an empty string");

    if (response.getCode() != 200) {
      generateError(response);
    }
    return createToken(body);
  }


  /**
   * {"code":0,"data":{"access_token":"u-BwIHe3ZGv0V36qQnjEbZeh","avatar_big":"https://s1-imfile.feishucdn.com/static-resource/v1/v2_a1f5fcfa-8c43-4c45-a739-0ffdb4fb051g~?image_size=640x640\u0026cut_type=\u0026quality=\u0026format=image\u0026sticker_format=.webp","avatar_middle":"https://s1-imfile.feishucdn.com/static-resource/v1/v2_a1f5fcfa-8c43-4c45-a739-0ffdb4fb051g~?image_size=240x240\u0026cut_type=\u0026quality=\u0026format=image\u0026sticker_format=.webp","avatar_thumb":"https://s3-imfile.feishucdn.com/static-resource/v1/v2_a1f5fcfa-8c43-4c45-a739-0ffdb4fb051g~?image_size=72x72\u0026cut_type=\u0026quality=\u0026format=image\u0026sticker_format=.webp","avatar_url":"https://s3-imfile.feishucdn.com/static-resource/v1/v2_a1f5fcfa-8c43-4c45-a739-0ffdb4fb051g~?image_size=72x72\u0026cut_type=\u0026quality=\u0026format=image\u0026sticker_format=.webp","en_name":"苏少华","expires_in":6900,"name":"苏少华","open_id":"ou_5bd4517a4a421bf9547a3f5b745426a0","refresh_expires_in":2591700,"refresh_token":"ur-N60F9NSfogqXcWRrxEWBfa","tenant_key":"2c2d68c6f2cf9758","token_type":"Bearer","union_id":"on_1f085d0c01b01adff71edf05da2620b6"},"msg":"success"}
   */
  private OAuth2AccessToken createToken(String rawResponse) throws IOException {

    final JsonNode response = OBJECT_MAPPER.readTree(rawResponse);

    final JsonNode dataNode = response.get("data");

    final JsonNode expiresInNode = dataNode.get("expires_in");
    final JsonNode refreshToken = dataNode.get(OAuthConstants.REFRESH_TOKEN);
    final JsonNode scope = dataNode.get(OAuthConstants.SCOPE);
    final JsonNode tokenType = dataNode.get("token_type");

    return createToken(extractRequiredParameter(dataNode, OAuthConstants.ACCESS_TOKEN, rawResponse).asText(),
        tokenType == null ? null : tokenType.asText(), expiresInNode == null ? null : expiresInNode.asInt(),
        refreshToken == null ? null : refreshToken.asText(), scope == null ? null : scope.asText(), dataNode,
        rawResponse);
  }

  @Override
  protected OAuth2AccessToken createToken(String accessToken, String tokenType, Integer expiresIn,
      String refreshToken, String scope, JsonNode response, String rawResponse) {
    var openId = extractRequiredParameter(response, "open_id", rawResponse).asText();
    var unionId = extractRequiredParameter(response, "union_id", rawResponse).asText();
//        var userId = extractRequiredParameter(response, "user_id", rawResponse).asText();
    var userId = "u_id";
    return new LarkToken(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse, openId, unionId, userId);
  }

}
