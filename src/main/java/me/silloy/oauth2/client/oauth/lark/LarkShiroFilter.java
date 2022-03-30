package me.silloy.oauth2.client.oauth.lark;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import static me.silloy.oauth2.client.properties.ShiroContants.LOGIN_BEFORE;
import static org.apache.shiro.web.util.WebUtils.toHttp;

/**
 * https://blog.csdn.net/qq_43332570/article/details/107003348
 */
@Slf4j
public class LarkShiroFilter extends UserFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginRequest(request, response)) {
            return true;
        } else {
            Subject subject = getSubject(request, response);
            return subject.getPrincipal() != null;
        }
    }


    // https://www.codeleading.com/article/7927568043/
    // https://github.com/axios/axios/issues/1322
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        saveRequest(request);
        redirectToLogin(request, response);
        return false;
    }

    @Override
    protected void saveRequest(ServletRequest request) {
        WebUtils.saveRequest(request);

        HttpServletRequest httpRequest = toHttp(request);
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();

        String referer = httpRequest.getHeader("referer");
        String requestUrl = httpRequest.getRequestURL().toString();

        if (StringUtils.hasLength(referer)) {
            log.debug("web request referer: {}， requestUrl：{}", referer, requestUrl);
            session.setAttribute(LOGIN_BEFORE, referer);
        } else {
            log.debug("web request requestUrl: {}", requestUrl);
            if (!StringUtils.endsWithIgnoreCase(requestUrl, "login") && !StringUtils.endsWithIgnoreCase(requestUrl, "logout")) {
                session.setAttribute(LOGIN_BEFORE, requestUrl);
            } else {
                session.setAttribute(LOGIN_BEFORE, "/");
            }
        }
    }

}
