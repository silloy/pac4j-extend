package me.silloy.oauth2.client.shiro;

import java.io.Serializable;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

public class ShiroSessionManager extends DefaultWebSessionManager {

  public final static String HEADER_TOKEN_NAME = "token";

  public ShiroSessionManager() {
    super();
  }

  @Override
  protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
    String id = WebUtils.toHttp(request).getHeader(HEADER_TOKEN_NAME);
    if (!StringUtils.hasLength(id)) {
      return super.getSessionId(request, response);
    } else {
      request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
      request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
      return id;
    }
  }
}
