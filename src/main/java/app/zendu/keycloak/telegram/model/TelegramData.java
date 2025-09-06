package app.zendu.keycloak.telegram.model;

public interface TelegramData {
    String id();
    String firstName();
    String lastName();
    String username();
    boolean validate(String botToken, long timeDelta);
}
