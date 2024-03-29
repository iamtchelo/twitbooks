import auth0 from 'auth0-js';
import client from '../api/client';

const domain = process.env.REACT_APP_AUTH_DOMAIN;
const clientId = process.env.REACT_APP_CLIENT_ID;
const audience = process.env.REACT_APP_AUDIENCE;
const redirectUri = process.env.REACT_APP_AUTH_CALLBACK_URI || 'http://localhost:3000/callback';
const logoutRedirect = process.env.REACT_APP_LOGOUT_REDIRECT_URI || 'http://localhost:3000/';

class Auth {
    constructor() {
        this.auth0 = new auth0.WebAuth({
            domain: domain,
            audience: audience,
            clientID: clientId,
            redirectUri: redirectUri,
            responseType: 'id_token',
            scope: 'openid profile'
        });

        this.getProfile = this.getProfile.bind(this);
        this.handleAuthentication = this.handleAuthentication.bind(this);
        this.isAuthenticated = this.isAuthenticated.bind(this);
        this.signIn = this.signIn.bind(this);
        this.signOut = this.signOut.bind(this);
    }

    getProfile() {
        return this.profile;
    }

    getIdToken() {
        return this.idToken;
    }

    isAuthenticated() {
        return new Date().getTime() < this.expiresAt;
    }

    signIn() {
        this.auth0.authorize();
    }

    handleAuthentication() {
        return new Promise((resolve, reject) => {
            this.auth0.parseHash((err, authResult) => {
                if (err) return reject(err);
                if (!authResult || !authResult.idToken) {
                    return reject(err);
                }
                this.setSession(authResult);
                resolve();
            });
        })
    }

    setSession(authResult) {
        this.idToken = authResult.idToken;
        this.profile = authResult.idTokenPayload;
        // set the time that the id token will expire at
        this.expiresAt = authResult.idTokenPayload.exp * 1000;
    }

    signOut() {
        this.auth0.logout({
            returnTo: logoutRedirect,
            clientID: clientId
        })
    }

    silentAuth() {
        return new Promise((resolve, reject) => {
            this.auth0.checkSession({}, (err, authResult) => {
                if (err) return reject(err);
                this.setSession(authResult);
                resolve();
            });
        });
    }

    validateAuthentication() {
        return client.login();
    }

}

let auth0Client = undefined;

const getAuthClient = () => {
    if (auth0Client === undefined) {
        auth0Client = new Auth();
    }
    return auth0Client;
};

export default getAuthClient;