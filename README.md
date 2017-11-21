## Okta OpenID Connect Fun!

This is a Spring Boot project that demonstrates various OIDC flows using
configurable response types and scopes.

Go to the live example at 
[https://okta-oidc-fun.herokuapp.com](https://okta-oidc-fun.herokuapp.com).

You can exchange an authorizaton code for tokens.

And, you can validate access and id tokens.

Want to run this app in your own Heroku instance? Click below:

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

You'll need to supply values for the following environment variables:

| Environment Variable         | Description                                                                                                           |
|------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| OKTA_ORG                     | The okta organization - ex: micah.okta.com                                                                            |
| OKTA_AUTHORIZATION_SERVER_ID | The Okta authorization server id - ex: aus9vmork8ww5twZg0h7                                                           |
| OKTA_OIDC_CLIENT_ID          | The Okta OIDC application client id                                                                                   |
| OKTA_OIDC_CLIENT_SECRET      | The Okta OIDC application client secret                                                                               |
| OKTA_SESSION_USERNAME        | The email address of the user that the app will establish a session as                                                |
| OKTA_SESSION_PASSWORD        | The password of the user that the app will establish a session as                                                     |
| OKTA_ZORK_URL                | The base URL of where the [Okta OAuth2 Zork](https://github.com/oktadeveloper/okta-zork-oauth-example) game is hosted. Note: If this is left blank, it will be ignored. |

The `OKTA_SESSION_USERNAME` and `OKTA_SESSION_PASSWORD` are used to establish a session
on the backend so that the user does not have to login to exercise the OIDC app.

These environment variables are stored on the heroku instance and are *not* available to the end user.

Whatever name you give your Heroku app, you must add the following redirects to your Okta OIDC app:

```
https://<your heroku app name>.herokuapp.com/continue
https://<your heroku app name>.herokuapp.com/flow_result
```

For instance, these are the supported redirects for where this app is currently deployed:

```
https://okta-oidc-fun.herokuapp.com/continue
https://okta-oidc-fun.herokuapp.com/flow_result
```

Go to the live example at 
[https://okta-oidc-fun.herokuapp.com](https://okta-oidc-fun.herokuapp.com).