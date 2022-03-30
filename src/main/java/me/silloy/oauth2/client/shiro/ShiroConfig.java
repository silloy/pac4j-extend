package me.silloy.oauth2.client.shiro;

import java.util.List;
import me.silloy.oauth2.client.oauth.lark.LarkClient;
import me.silloy.oauth2.client.oauth.lark.LarkSavedRequestHandler;
import me.silloy.oauth2.client.oauth.lark.LarkShiroFilter;
import me.silloy.oauth2.client.properties.AuthConfig;
import io.buji.pac4j.filter.CallbackFilter;
import io.buji.pac4j.filter.SecurityFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebFilterConfiguration;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.pac4j.core.config.Config;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ShiroConfig extends ShiroWebFilterConfiguration {

  private final AuthConfig config;

  @Override
  protected ShiroFilterFactoryBean shiroFilterFactoryBean() {
    ShiroFilterFactoryBean shiroFilterFactoryBean = super.shiroFilterFactoryBean();
    CallbackFilter callbackFilter = new CallbackFilter();
    Config larkClientConfig = new Config(larkClient());

    DefaultSecurityLogic securityLogic = DefaultSecurityLogic.INSTANCE;
    securityLogic.setSavedRequestHandler(new LarkSavedRequestHandler());
    larkClientConfig.setSecurityLogic(securityLogic);

    callbackFilter.setConfig(larkClientConfig);
    SecurityFilter ssoFilter = new SecurityFilter();
    ssoFilter.setConfig(larkClientConfig);

    shiroFilterFactoryBean.getFilters().put("authc", new LarkShiroFilter());
    shiroFilterFactoryBean.getFilters().put("sso", ssoFilter);
    shiroFilterFactoryBean.getFilters().put("callbackFilter", callbackFilter);
    Map<String, String> map = new LinkedHashMap<>();
    map.put("/lark", "sso");
    map.put("/callback", "callbackFilter");
    map.put("/login**", "anon");
    map.put("/sign**", "anon");
    map.put("/**", "authc");

    shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
    return shiroFilterFactoryBean;
  }

  @Bean
  public LarkClient larkClient() {
    return new LarkClient(config.getLark());
  }

  @Bean
  public Realm realm() {
    ShiroRealm realm = new ShiroRealm();
    realm.setCachingEnabled(true);
    realm.setAuthenticationCachingEnabled(true);
    realm.setAuthenticationCacheName("authenticationCache");
    realm.setAuthorizationCachingEnabled(true);
    realm.setAuthorizationCacheName("authorizationCache");
    return realm;
  }



  //  other http://alexxiyang.github.io/shiro-redis/

  /**
   Title	Default	Description
   shiro-redis.enabled	true	Enables shiro-redis’s Spring module
   shiro-redis.redis-manager.deploy-mode	standalone	Redis deploy mode. Options: standalone, sentinel, ‘cluster’
   shiro-redis.redis-manager.host	127.0.0.1:6379	Redis host. If you don’t specify host the default value is 127.0.0.1:6379. If you run redis in sentinel mode or cluster mode, separate host names with comma, like 127.0.0.1:26379,127.0.0.1:26380,127.0.0.1:26381
   shiro-redis.redis-manager.master-name	mymaster	Only used for sentinel mode, The master node of Redis sentinel mode
   shiro-redis.redis-manager.timeout	2000	Redis connect timeout. Timeout for jedis try to connect to redis server(In milliseconds)
   shiro-redis.redis-manager.so-timeout	2000	Only used for sentinel mode or cluster mode, The timeout for jedis try to read data from redis server
   shiro-redis.redis-manager.max-attempts	3	Only used for cluster mode, Max attempts to connect to server
   shiro-redis.redis-manager.password	 	Redis password shiro-redis.redis-manager.database	0	Redis database. Default value is 0
   shiro-redis.redis-manager.count	100	Scan count. Shiro-redis use Scan to get keys, so you can define the number of elements returned at every iteration.
   shiro-redis.session-dao.expire	-2	Redis cache key/value expire time. The expire time is in second.
       Special values:
       -1: no expire
       -2: the same timeout with session
       Default value: -2
       Note: Make sure expire time is longer than session timeout.
   shiro-redis.session-dao.key-prefix	shiro:session:	Custom your redis key prefix for session management
       Note: Remember to add colon at the end of prefix.
   shiro-redis.session-dao.session-in-memory-timeout	1000	When we do signin, doReadSession(sessionId) will be called by shiro about 10 times. So shiro-redis save Session in ThreadLocal to remit this problem. sessionInMemoryTimeout is expiration of Session in ThreadLocal.
       Most of time, you don’t need to change it.
   shiro-redis.session-dao.session-in-memory-enabled	true	Whether or not enable temporary save session in ThreadLocal
   shiro-redis.cache-manager.principal-id-field-name	id	Principal id field name. The field which you can get unique id to identify this principal.
       For example, if you use UserInfo as Principal class, the id field maybe id, userId, email, etc.
       Remember to add getter to this id field. For example, getId(), getUserId(), getEmail(), etc.
       Default value is id, that means your principal object must has a method called getId()
   shiro-redis.cache-manager.expire	1800	Redis cache key/value expire time.
       The expire time is in second.
   shiro-redis.cache-manager.key-prefix	shiro:cache:	Custom your redis key prefix for cache management
       Note: Remember to add colon at the end of prefix.
   */
  @Bean
  public SessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
    ShiroSessionManager sessionManager = new ShiroSessionManager();
    sessionManager.setSessionDAO(redisSessionDAO);
    return sessionManager;
  }

  @Bean
  public DefaultWebSecurityManager securityManager(RedisCacheManager redisCacheManager, SessionManager sessionManager, List<Realm> realms) {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realms);
    securityManager.setSessionManager(sessionManager);
    securityManager.setCacheManager(redisCacheManager);
    return securityManager;
  }

  /**
   * 开启Shiro注解模式，可以在Controller中的方法上添加注解
   */
  @Bean
  public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultSecurityManager securityManager) {
    AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
    authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
    return authorizationAttributeSourceAdvisor;
  }



}
