package com.squonk.security.impl;

import com.squonk.security.UserDetails;
import com.squonk.security.UserDetailsManager;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;

/**
 *
 * @author timbo
 */
public class KeycloakUserDetailsManager implements UserDetailsManager {

    @Override
    public UserDetails getAuthenticatedUser(HttpServletRequest request) {
        KeycloakSecurityContext session = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        
        IDToken idToken = session.getIdToken();
        if (idToken == null) {
            return null;
        }

        AccessToken accessToken = session.getToken();
        // get roles from accessToken ?
       
        return new UserDetails(idToken.getPreferredUsername(), idToken.getEmail(), idToken.getGivenName(), idToken.getFamilyName());
    }

    @Override
    public URI getLogoutUrl(HttpServletRequest request, String redirectTo) {
        KeycloakSecurityContext session = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        IDToken token = session.getIdToken();
        if (token == null) {
            return null;
        }
        
        String realm = session.getRealm();
        String issuer = token.getIssuer(); // https://192.168.59.103/auth/realms/samplerealm
        // ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH = "/realms/{realm-name}/protocol/openid-connect/logout";
         URI uri = KeycloakUriBuilder.fromUri(issuer).path("/protocol/openid-connect/logout").queryParam("redirect_uri", redirectTo).build(realm);
        
        // returns something like this:
        // https://192.168.59.103/auth/realms/samplerealm/protocol/openid-connect/logout?redirect_uri=http%3A%2F%2F192.168.59.103%3A8080%2Fsampleapp%2Findex.html
        return uri;
    }
}
