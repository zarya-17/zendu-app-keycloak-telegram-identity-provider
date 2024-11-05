package ru.spliterash.keycloakTelegram.validator;

import ru.spliterash.keycloakTelegram.model.AuthParameter;
import ru.spliterash.keycloakTelegram.model.TelegramAuthData;
import ru.spliterash.keycloakTelegram.model.TelegramWebAuthenticatorConfig;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@JBossLog
public class TelegramAuthDataValidator {
    private static final String KEY_CRYPTO_ALGORITHM = "SHA-256";
    private static final String MAIN_CRYPTO_ALGORITHM = "HmacSHA256";

    private static final Clock clock = Clock.system(ZoneId.systemDefault());

    private final TelegramWebAuthenticatorConfig authenticatorConfig;
    private final TelegramAuthData telegramAuthData;

    public TelegramAuthDataValidator(TelegramWebAuthenticatorConfig authenticatorConfig, TelegramAuthData telegramAuthData) {
        this.authenticatorConfig = authenticatorConfig;
        this.telegramAuthData = telegramAuthData;
    }

    public boolean isValid() {
        try {
            if (!checkAuthDate()) {
                throw new IllegalArgumentException("Authentication request expired!");
            }

            if (!checkHash()) {
                throw new IllegalArgumentException("Invalid hash!");
            }

            return true;
        } catch (Exception e) {
            log.error("Invalid authentication data!", e);
        }

        return false;
    }

    private boolean checkAuthDate() {
        long currentEpochSeconds = clock.instant().getEpochSecond();
        long authDateSeconds = Long.parseLong(telegramAuthData.authDate());
        return currentEpochSeconds - authenticatorConfig.getAuthTimeDelta() <= authDateSeconds;
    }

    private boolean checkHash() throws Exception {
        return Objects.equals(telegramAuthData.hash(), calculateHash(telegramAuthData));
    }

    private String calculateHash(TelegramAuthData telegramAuthData) throws Exception {
        Map<String, String> values = new TreeMap<>();
        values.put(AuthParameter.ID_FIELD_NAME.queryName, telegramAuthData.id());
        values.put(AuthParameter.FIRST_NAME_FIELD_NAME.queryName, telegramAuthData.firstName());
        values.put(AuthParameter.LAST_NAME_FIELD_NAME.queryName, telegramAuthData.lastName());
        values.put(AuthParameter.USERNAME_FIELD_NAME.queryName, telegramAuthData.username());
        values.put(AuthParameter.PHOTO_URL_FIELD_NAME.queryName, telegramAuthData.photoUrl());
        values.put(AuthParameter.AUTH_DATE_FIELD_NAME.queryName, telegramAuthData.authDate());

        String checkString = values.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("\n"));

        return Hex.encodeHexString(getMacInstance().doFinal(checkString.getBytes(StandardCharsets.UTF_8)));
    }

    private Mac getMacInstance() throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                MessageDigest.getInstance(KEY_CRYPTO_ALGORITHM).digest(authenticatorConfig.getClientSecret().getBytes(UTF_8)),
                KEY_CRYPTO_ALGORITHM
        );
        Mac mac = Mac.getInstance(MAIN_CRYPTO_ALGORITHM);
        mac.init(secretKeySpec);
        return mac;
    }
}
