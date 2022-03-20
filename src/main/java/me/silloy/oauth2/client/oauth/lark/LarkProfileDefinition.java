package me.silloy.oauth2.client.oauth.lark;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.Token;
import java.util.Arrays;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

public class LarkProfileDefinition extends OAuthProfileDefinition {

  public static final String AVATAR_URL = "avatar_url";

  public static final String NAME = "name";

  public static final String OPENID = "open_id";

  public static final String EN_NAME = "en_name";

  public static final String TENANT_KEY = "tenant_key";

  public static final String UNION_ID = "union_id";

  public LarkProfileDefinition() {
    Arrays.stream(new String[]{
        NAME,
        OPENID,
        EN_NAME,
        TENANT_KEY,
        UNION_ID,
    }).forEach(a -> primary(a, Converters.STRING));
    primary(AVATAR_URL, Converters.URL);
  }

  private final String LARK_PROFILE_URL = "https://open.feishu.cn/open-apis/authen/v1/user_info";

  @Override
  public String getProfileUrl(Token token, OAuthConfiguration oAuthConfiguration) {
    return LARK_PROFILE_URL;
  }

  @Override
  public LarkProfile extractUserProfile(String body) {
    final LarkProfile profile = new LarkProfile();
    final JsonNode json = JsonHelper.getFirstNode(body);
    if (json != null) {
      final JsonNode codeJson = json.get("code");
      if (codeJson != null && codeJson.intValue() > 0) {
        final JsonNode msgJson = json.get("msg");
        throw new OAuthException(
            msgJson.toString() != null ? msgJson.toString() : "error code " + codeJson.intValue());
      }
      final JsonNode dataJson = json.get("data");
//            profile.setId(ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, "id")));
      for (final String attribute : getPrimaryAttributes()) {
        convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(dataJson, attribute));
      }
    } else {
      raiseProfileExtractionJsonError(body);
    }
    return profile;
  }

}
