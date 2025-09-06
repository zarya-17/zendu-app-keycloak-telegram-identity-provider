package app.zendu.keycloak.telegram.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Base64;

@RequiredArgsConstructor
public class TelegramDataFactory {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public TelegramData decodeData(String encoded, boolean isMiniApp) {
        String decoded = new String(Base64.getDecoder().decode(safeBase64ToRegular(encoded)));
        return isMiniApp ? new TelegramMiniAppInitData(decoded) : objectMapper
                .readValue(decoded,TelegramAuthData.class);
    }

    private static String safeBase64ToRegular(String safe) {
        return safe.replaceAll("-", "+").replaceAll("_", "/");
    }
}
