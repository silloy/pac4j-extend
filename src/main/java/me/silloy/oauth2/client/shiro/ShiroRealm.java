package me.silloy.oauth2.client.shiro;

import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.token.Pac4jToken;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.pac4j.core.profile.UserProfile;

//@Component
public class ShiroRealm extends Pac4jRealm {

  private static final String USERNAME = "u";
  private static final String PASSWORD = "p";
  private static final Collection<String> ROLES = new HashSet<>();
  private static final Collection<String> PERMISSIONS = new HashSet<>();

  @Getter
  @Setter
  private String principalNameAttribute;

  static {
    ROLES.add("admin");
    PERMISSIONS.add("system:user:add");
  }

  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
    authorizationInfo.addRoles(ROLES);
    authorizationInfo.addStringPermissions(PERMISSIONS);
    return authorizationInfo;
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    if (token instanceof Pac4jToken) {
      Pac4jToken pac4jToken = (Pac4jToken) token;
      List<UserProfile> profiles = pac4jToken.getProfiles();
      Pac4jPrincipal principal = new Pac4jPrincipal(profiles, this.principalNameAttribute);
      PrincipalCollection principalCollection = new SimplePrincipalCollection(principal, this.getName());
      return new SimpleAuthenticationInfo(principalCollection, profiles.hashCode());
    }

    if (token instanceof UsernamePasswordToken) {
      UsernamePasswordToken upt = (UsernamePasswordToken) token;
      if (Objects.equals(upt.getPrincipal(), USERNAME) && Objects.equals(PASSWORD, new String(upt.getPassword()))) {
        return new SimpleAuthenticationInfo(upt.getPrincipal(), upt.getCredentials(), getName());
      }
    }
    throw new AuthenticationException("login fail");
  }
}
