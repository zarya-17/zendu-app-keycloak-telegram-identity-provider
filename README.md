# Telegram Identity Provider

The project allows you to add telegram as an identity provider to the keycloak.

This IdP is compatible with telegram login widget and telegram mini apps.

Project is fork of https://github.com/Spliterash/keycloak-telegram-identity-provider (author [Spliterash](https://github.com/Spliterash))
which is fork of  https://github.com/rickispp/telegram-web-keycloak-authenticator (author [Valentin Paydulov](https://github.com/rickispp))

## Setup

### Keycloak setup

1) Add telegram identity provider to you realm
2) Pass bot token to client's secret
3) (Optionally) Setup redirection link to page with telegram widget
4) Setup keycloak client
5) Duplicate browser flow in authentication tab
6) Add execution `Pass telegram data` as alternative and place it before execution `Cookie`
7) Save flow and bind as browser flow

### Setup frontend

To pass telegram data you need to encode this data with base64. And there is two options,depends on where you need
implement verification:

* Telegram login widget: you need to encode whole `user` parameter in function, that passed to `data-onauth`
* Telegram mini-app: you need to encode whole `decodeURIComponent(window.Telegram.WebApp.initData)`

Encoded data should be passed as `tg_encoded_data` parameter

This parameter can be applied to endpoints:

* `keycloak-host/realms/realm-name/protocol/openid-connect/auth`
* `keycloak-host/realms/realm-name/broker/idp-alias/endpoint`

If `tg_encoded_data` wouldn't be passed to first endpoint user will be redirected to redirection url (see keycloak setup).

For more info see examples.
