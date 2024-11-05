package ru.spliterash.keycloakTelegram;

import ru.spliterash.keycloakTelegram.model.TelegramWebAuthenticatorConfig;
import com.google.auto.service.AutoService;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.provider.IdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

@AutoService(IdentityProviderFactory.class)
public class TelegramWebLoginWidgetIdentityProviderFactory extends AbstractIdentityProviderFactory<TelegramWebLoginWidgetIdentityProvider> {
    public static String AUTHENTICATOR_ID = "telegram";
    public static final String AUTH_TIME_DELTA_CONFIG_NAME = "telegram_auth_time_delta";

    public static final String DEFAULT_AUTH_TIME_DELTA = "60";


    @Override
    public String getId() {
        return AUTHENTICATOR_ID;
    }

    @Override
    public String getName() {
        return "Telegram";
    }

    @Override
    public String getHelpText() {
        return "Authenticator that allows you to login using Telegram Web Login Widget";
    }

    @Override
    public TelegramWebLoginWidgetIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new TelegramWebLoginWidgetIdentityProvider(session, new TelegramWebAuthenticatorConfig(model));
    }

    @Override
    public IdentityProviderModel createConfig() {
        return new TelegramWebAuthenticatorConfig();
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
                .build();
    }
}
