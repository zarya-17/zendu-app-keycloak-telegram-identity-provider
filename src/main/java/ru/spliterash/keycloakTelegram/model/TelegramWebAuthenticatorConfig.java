package ru.spliterash.keycloakTelegram.model;

import lombok.NoArgsConstructor;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;
import ru.spliterash.keycloakTelegram.TelegramWebLoginWidgetIdentityProviderFactory;

@NoArgsConstructor
public class TelegramWebAuthenticatorConfig extends OAuth2IdentityProviderConfig {
    public TelegramWebAuthenticatorConfig(IdentityProviderModel model) {
        super(model);
    }

    public Long getAuthTimeDelta() {
        return Long.parseLong(getConfig().getOrDefault(TelegramWebLoginWidgetIdentityProviderFactory.AUTH_TIME_DELTA_CONFIG_NAME, TelegramWebLoginWidgetIdentityProviderFactory.DEFAULT_AUTH_TIME_DELTA));
    }

    public void setAuthTimeDelta(Long authTimeDelta) {
        getConfig().put(TelegramWebLoginWidgetIdentityProviderFactory.AUTH_TIME_DELTA_CONFIG_NAME, authTimeDelta.toString());
    }
}
