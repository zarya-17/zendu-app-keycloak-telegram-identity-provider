package app.zendu.keycloak.telegram.authenricator;

import jakarta.ws.rs.core.MultivaluedMap;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;


public class TGDataEncodedParamAuthenticator implements Authenticator {
    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {
        MultivaluedMap<String, String> params = authenticationFlowContext.getHttpRequest().getUri().getQueryParameters();
        String customParam = params.getFirst("tg_encoded_data");

        if (customParam != null) {
            authenticationFlowContext.getAuthenticationSession().setUserSessionNote("tg_encoded_data", customParam);
        }

        authenticationFlowContext.attempted();
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {

    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }
}
