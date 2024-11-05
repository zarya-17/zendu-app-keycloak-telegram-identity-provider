# Telegram Widget Identity Provider

The project allows you to add telegram as an identity provider to the keycloak
![form.png](.github/images/login_form.png)

## Setup

1) Select `keycloak.v2-telegram-web-login` theme
2) Add telegram identity provider. <b>Specify alias</b> like your bot name, its important
   ![setup.png](.github/images/identity_provider.png)
3) Done, you can now register via telegram

PS: I do not override account theme, because i don't need it, and actually don't know how. Button available only on
login
page

This project is a hard fork of https://github.com/rickispp/telegram-web-keycloak-authenticator