package app.zendu.keycloak.telegram;

import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class TelegramIdentityProviderFactory extends AbstractIdentityProviderFactory<TelegramIdentityProvider> {
    public static final String PROVIDER_ID = "telegram";

    public static final String AUTH_TIME_DELTA_CONFIG_NAME = "telegram_auth_time_delta";

    public static final String DEFAULT_AUTH_TIME_DELTA = "60";

    public static final String REDIRECTION_LINK_CONFIG_NAME = "telegram_redirection_link";

    public static final String EMAIL_SUBSTITUTION_CONFIG_NAME = "telegram_email_substitution";

    @Override
    public String getName() {
        return "Telegram";
    }

    @Override
    public TelegramIdentityProvider create(KeycloakSession keycloakSession, IdentityProviderModel identityProviderModel) {
        return new TelegramIdentityProvider(keycloakSession, identityProviderModel);
    }

    @Override
    public IdentityProviderModel createConfig() {
        return new IdentityProviderModel();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property()
                .name(AUTH_TIME_DELTA_CONFIG_NAME)
                .label("Auth time delta (in seconds)")
                .helpText("""
                        The maximum delta (in seconds) between the time of successful authorization in Telegram ('auth_date' parameter) and the time of receive the authorization request in Keycloak.
                        After this time, request will be considered expired, even if it contains valid data.
                        This is necessary to avoid reuse of authorization data in case of leakage.
                        """)
                .type(ProviderConfigProperty.STRING_TYPE)
                .secret(false)
                .required(true)
                .defaultValue(DEFAULT_AUTH_TIME_DELTA)
                .add()
                .property()
                .name(REDIRECTION_LINK_CONFIG_NAME)
                .label("Redirection link to page with telegram login button")
                .helpText("""
                        Redirection link to page with telegram login button.
                        If you for some reasons don't want to embed telegram button into you login page, you can setup redirect link. \n
                        Leave it empty, if redirection is not needed. \n
                        IMPORTANT: double check link. Automation url check is NOT performed.
                        """)
                .type(ProviderConfigProperty.STRING_TYPE)
                .secret(false)
                .required(false)
                .add()
                .property()
                .name(EMAIL_SUBSTITUTION_CONFIG_NAME)
                .label("Email substitution postfix")
                .helpText("""
                        Telegram doesn't provide email, which is needed for keycloak. With using this parameter
                        keycloak is able to generate fake email with format temp-<telegram_id><postfix>. \n
                        Leave it empty to omit fake email generation.
                        """)
                .type(ProviderConfigProperty.STRING_TYPE)
                .secret(false)
                .required(false)
                .add()
                .build();
    }
}
