# Okta OpenID Connect sandbox example

This is a Spring Boot project that demonstrates various OpenID Connect flows using
configurable response types and scopes.

If you want to learn more about OpenID Connect, read the article series that accompanies this example:

* [Identity, Claims, & Tokens – An OpenID Connect Primer, Part 1 of 3](https://developer.okta.com/blog/2017/07/25/oidc-primer-part-1)
* [OIDC in Action – An OpenID Connect Primer, Part 2 of 3](https://developer.okta.com/blog/2017/07/25/oidc-primer-part-2)
* [What’s in a Token? – An OpenID Connect Primer, Part 3 of 3](https://developer.okta.com/blog/2017/08/01/oidc-primer-part-3)

## Try it out

Go to the live example at 
[https://okta-oidc-fun.herokuapp.com](https://okta-oidc-fun.herokuapp.com).

You can exchange an authorizaton code for tokens.

And, you can validate access and id tokens.

## Deploy it yourself

Want to run this app in your own Heroku instance? Click below:

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

You'll need to supply values for the following environment variables:

| Environment Variable         | Description                                                                                                           |
|------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| OKTA_ORG                     | The okta organization - ex: micah.okta.com                                                                            |
| OKTA_AUTHORIZATION_SERVER_ID | The Okta authorization server id - ex: `default`                                                           |
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

## Help

Please post any questions on our [Okta Developer Forums](https://devforum.okta.com/). You can also email developers@okta.com if you would like to create a support ticket.

## License

Apache 2.0, see [LICENSE](LICENSE).
