package me.silloy.oauth2.client.oauth.lark;

import java.net.URI;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * customize profile properties
 */
public class LarkProfile extends OAuth20Profile {

  @Override
  public String getUsername() {
    return (String) getAttribute(LarkProfileDefinition.NAME);
  }

  @Override
  public URI getPictureUrl() {
    return (URI) getAttribute(LarkProfileDefinition.AVATAR_URL);
  }

}
