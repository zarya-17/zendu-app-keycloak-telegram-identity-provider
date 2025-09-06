package app.zendu.keycloak.telegram.model;

import app.zendu.keycloak.telegram.crypt.CryptUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.StringJoiner;

public class TelegramMiniAppInitData implements TelegramData {
    private static final String HASH_PREFIX = "hash=";
    private static final String USER_PREFIX = "user=";
    private static final String AUTH_DATE_PREFIX = "auth_date=";
    private static final String WEB_APP_DATA = "WebAppData";

    private final String validationString;
    private final UserInfo userInfo;
    private final String hash;
    private final Instant authDate;

    @SneakyThrows
    public TelegramMiniAppInitData(String rawData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String[] chunks = rawData.split("&");
        Arrays.sort(chunks);
        StringJoiner stringJoiner = new StringJoiner("\n");
        String hash = null;
        UserInfo userInfo = null;
        Instant authDate = null;
        for (String chunk : chunks) {
            if (chunk.startsWith(HASH_PREFIX)) {
                hash = chunk.substring(HASH_PREFIX.length());
                continue; //hash should not be included into data for verification
            }

            if (chunk.startsWith(AUTH_DATE_PREFIX)) {
                authDate = Instant.ofEpochSecond(Long.parseLong(chunk.substring(AUTH_DATE_PREFIX.length())));
            }

            if (chunk.startsWith(USER_PREFIX)) {
                userInfo = objectMapper.readValue(chunk.substring(USER_PREFIX.length()), UserInfo.class);
            }
            stringJoiner.add(chunk);
        }
        validationString = stringJoiner.toString();
        if (userInfo == null || hash == null || authDate == null) {
            throw new IllegalArgumentException("Hash, auth date or/and user data is missed");
        }
        this.userInfo = userInfo;
        this.hash = hash;
        this.authDate = authDate;
    }

    @Override
    public String id() {
        return userInfo.id;
    }

    @Override
    public String firstName() {
        return userInfo.firstName;
    }

    @Override
    public String lastName() {
        return userInfo.lastName;
    }

    @Override
    public String username() {
        return userInfo.username;
    }

    @Override
    public boolean validate(String botToken, long timeDelta) {
        if (Duration.between(authDate, Instant.now()).getSeconds() > timeDelta)
            return false;

        byte[] botTokenSignature = CryptUtils.hmacSha256byte(botToken, WEB_APP_DATA.getBytes());
        return hash.equals(CryptUtils.hmacSha256(validationString, botTokenSignature));
    }

    public record UserInfo(String id,
                           @JsonProperty("first_name") String firstName,
                           @JsonProperty("last_name") String lastName,
                           String username,
                           @JsonProperty("photo_url") String photoUrl) {
    }


}
