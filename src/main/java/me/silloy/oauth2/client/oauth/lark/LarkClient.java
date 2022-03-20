package me.silloy.oauth2.client.oauth.lark;

import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.oauth.client.OAuth20Client;


/**
 * @see org.pac4j.oauth.client.GitHubClient
 */
public class LarkClient extends OAuth20Client {

  private final LarkProperties properties;

  public LarkClient(LarkProperties properties) {
    this.properties = properties;
    setKey(properties.getKey());
    setSecret(properties.getSecret());

    setUrlResolver(new DefaultUrlResolver(true));
    setCallbackUrl(properties.getCallbackUrl());
  }


  @Override
  protected void internalInit(final boolean forceReinit) {
    configuration.setApi(new LarkApi());
    configuration.setProfileDefinition(new LarkProfileDefinition());
    configuration.setWithState(true);
    defaultProfileCreator(new LarkProfileCreator(configuration, this));

    super.internalInit(forceReinit);
  }


}
