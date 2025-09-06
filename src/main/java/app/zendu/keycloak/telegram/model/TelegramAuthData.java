package app.zendu.keycloak.telegram.model;

import app.zendu.keycloak.telegram.crypt.CryptUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record TelegramAuthData(
        String id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String username,
        @JsonProperty("photo_url") String photoUrl,
        @JsonProperty("auth_date") String authDate,
        String hash
) implements TelegramData {
    private static String getFormatedValue(String prefix, String value) {
        return value == null ? null : prefix + value;
    }

    private String getFormatedId() {
        return getFormatedValue("id=", id);
    }

    private String getFormatedAuthDate() {
        return getFormatedValue("auth_date=", authDate);
    }

    private String getFormatedFirstName() {
        return getFormatedValue("first_name=", firstName);
    }

    private String getFormatedLastName() {
        return getFormatedValue("last_name=", lastName);
    }

    private String getFormatedUsername() {
        return getFormatedValue("username=", username);
    }

    private String getFormatedPhotoUrl() {
        return getFormatedValue("photo_url=", photoUrl);
    }

    private String formatAuthValue() {
        List<String> sorted = Stream.of(
                this.getFormatedId(),
                this.getFormatedAuthDate(),
                this.getFormatedLastName(),
                this.getFormatedFirstName(),
                this.getFormatedUsername(),
                this.getFormatedPhotoUrl()
        ).filter(Objects::nonNull).sorted().toList();
        return String.join("\n", sorted);
    }

    @Override
    public boolean validate(String botToken, long timeDelta) {
        if (Duration.between(Instant.ofEpochSecond(Long.parseLong(authDate)), Instant.now())
                .getSeconds() > timeDelta)
            return false;

        byte[] botTokenSignature = CryptUtils.sha256(botToken);
        return hash.equals(CryptUtils.hmacSha256(formatAuthValue(), botTokenSignature));
    }
}
