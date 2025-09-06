package app.zendu.keycloak.telegram;

import jakarta.ws.rs.core.Response;
import lombok.extern.jbosslog.JBossLog;
import app.zendu.keycloak.telegram.callback.Callback;
import org.keycloak.broker.provider.AbstractIdentityProvider;
import org.keycloak.broker.provider.AuthenticationRequest;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.FederatedIdentityModel;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import java.net.URI;

@JBossLog
public class TelegramIdentityProvider extends AbstractIdentityProvider<IdentityProviderModel> {
    private final String botToken;
    private final String ownHost;
    private final String alias;
    private final long timeDelta;
    private final String realmName;
    private final String redirectUrl;
    private final String emailPostfix;

    public TelegramIdentityProvider(KeycloakSession session, IdentityProviderModel config) {
        super(session, config);
        botToken = config.getConfig().get("clientSecret");
        alias = config.getAlias();
        realmName = session.getContext().getRealm().getName();
        log.info(config.getConfig().get(TelegramIdentityProviderFactory.REDIRECTION_LINK_CONFIG_NAME));
        ownHost = session.getContext().getUri().getBaseUri().toASCIIString().replaceAll("/$", "");
        timeDelta = Long.parseLong(config.getConfig().getOrDefault(TelegramIdentityProviderFactory.AUTH_TIME_DELTA_CONFIG_NAME, TelegramIdentityProviderFactory.DEFAULT_AUTH_TIME_DELTA));
        redirectUrl = config.getConfig().get(TelegramIdentityProviderFactory.REDIRECTION_LINK_CONFIG_NAME);
        emailPostfix = config.getConfig().get(TelegramIdentityProviderFactory.EMAIL_SUBSTITUTION_CONFIG_NAME);
    }

    @Override
    public Response retrieveToken(KeycloakSession keycloakSession, FederatedIdentityModel federatedIdentityModel) {
        throw new IllegalStateException("telegram doesn't retrieve tokes");
    }

    @Override
    public Response performLogin(AuthenticationRequest request) {
        String tgEncodedData = request.getAuthenticationSession().getUserSessionNotes().get("tg_encoded_data");
        if (tgEncodedData != null) {
            URI uri = URI
                    .create(String.format("%s/realms/%s/broker/%s/endpoint?tg_encoded_data=%s&state=%s&mini_app=true", ownHost, realmName, alias, tgEncodedData, request.getState().getEncoded()));
            return Response.temporaryRedirect(uri)
                    .build();
        }

        if (redirectUrl == null) {
            throw new IllegalStateException("redirect uri is not configured");
        }

        return Response.temporaryRedirect(URI
                        .create(String.format("%s?state=%s", redirectUrl, request.getState().getEncoded())))
                .build();
    }

    @Override
    public Object callback(RealmModel realm, AuthenticationCallback callback, EventBuilder event) {
        return new Callback(botToken, callback, this.session, this, timeDelta, emailPostfix);
    }
}
