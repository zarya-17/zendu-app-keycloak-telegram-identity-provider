package ru.spliterash.keycloakTelegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TelegramAuthData(
        String id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String username,
        @JsonProperty("photo_url") String photoUrl,
        @JsonProperty("auth_date") String authDate,
        String hash
) {
}

