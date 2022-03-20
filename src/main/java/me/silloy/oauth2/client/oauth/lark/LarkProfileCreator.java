package me.silloy.oauth2.client.oauth.lark;

import com.github.scribejava.core.model.Token;
import java.util.Optional;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

public class LarkProfileCreator extends OAuth20ProfileCreator {

  public LarkProfileCreator(OAuth20Configuration configuration, IndirectClient client) {
    super(configuration, client);
  }

  @Override
  protected Optional<UserProfile> retrieveUserProfileFromToken(final WebContext context, final Token accessToken) {
    final var token = (LarkToken) accessToken;
    final var profile = super.retrieveUserProfileFromToken(context, token);
    profile.get().setId(token.getOpenId());
    return profile;
  }
}
