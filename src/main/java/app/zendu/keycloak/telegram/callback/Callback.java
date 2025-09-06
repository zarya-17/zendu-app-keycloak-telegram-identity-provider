package app.zendu.keycloak.telegram.callback;

import app.zendu.keycloak.telegram.TelegramIdentityProvider;
import app.zendu.keycloak.telegram.model.TelegramData;
import app.zendu.keycloak.telegram.model.TelegramDataFactory;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityProvider;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.sessions.AuthenticationSessionModel;

@JBossLog
@RequiredArgsConstructor
public class Callback {
    private final String botToken;
    private final IdentityProvider.AuthenticationCallback callback;
    private final KeycloakSession session;
    private final TelegramIdentityProvider identityProvider;
    private final long timeDelta;
    private final String emailPostfix;

    private final TelegramDataFactory telegramDataFactory = new TelegramDataFactory();

    @GET
    @Path("/")
    @SneakyThrows
    @SuppressWarnings("unused")
    public Response auth(
            @QueryParam("tg_encoded_data") String tgEncodedData,
            @QueryParam("mini_app") boolean isMiniApp,
            @QueryParam("state") String state
    ) {
        AuthenticationSessionModel authSession = this.callback.getAndVerifyAuthenticationSession(state);

        session.getContext().setAuthenticationSession(authSession);

        IdentityProviderModel authenticatorConfig = identityProvider.getConfig();

        TelegramData telegramAuthData = telegramDataFactory.decodeData(tgEncodedData, isMiniApp);

        if (!telegramAuthData.validate(botToken, timeDelta)) {
            return callback.cancelled(authenticatorConfig);
        }

        BrokeredIdentityContext context = new BrokeredIdentityContext(telegramAuthData.id(), authenticatorConfig);
        context.setUsername(telegramAuthData.username());
        context.setFirstName(telegramAuthData.firstName());
        context.setLastName(telegramAuthData.lastName());
        context.setIdp(identityProvider);
        if (emailPostfix != null)
            context.setEmail("temp-" + telegramAuthData.id() + emailPostfix);
        context.setAuthenticationSession(authSession);
        return callback.authenticated(context);
    }
}
