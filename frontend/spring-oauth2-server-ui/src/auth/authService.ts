import { UserManager, WebStorageStateStore, Log } from 'oidc-client-ts';

const oidcConfig = {
    authority: 'http://127.0.0.1:8080',
    client_id: 'spring-oauth2-server-ui',
    client_secret: 'secret',
    redirect_uri: 'http://localhost:5173/callback',
    post_logout_redirect_uri: 'http://localhost:5173/',
    response_type: 'code',
    scope: 'openid read',
    code_challenge_method: null,
    userStore: new WebStorageStateStore({ store: window.localStorage }),
};

Log.setLogger(console);
Log.setLevel(Log.DEBUG);

export const userManager = new UserManager(oidcConfig);