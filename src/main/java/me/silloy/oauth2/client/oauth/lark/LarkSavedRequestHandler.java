package me.silloy.oauth2.client.oauth.lark;

import lombok.extern.slf4j.Slf4j;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.savedrequest.DefaultSavedRequestHandler;
import org.pac4j.core.util.Pac4jConstants;

import static me.silloy.oauth2.client.properties.ShiroContants.LOGIN_BEFORE;

@Slf4j
public class LarkSavedRequestHandler extends DefaultSavedRequestHandler {


    @Override
    public void save(final WebContext context, final SessionStore sessionStore) {
        var loginBeforeUrlOptional = sessionStore.get(context, LOGIN_BEFORE);
        if (loginBeforeUrlOptional.isPresent()) {
            sessionStore.set(context, Pac4jConstants.REQUESTED_URL, loginBeforeUrlOptional.get());
        } else {
            super.save(context, sessionStore);
        }
    }


}
