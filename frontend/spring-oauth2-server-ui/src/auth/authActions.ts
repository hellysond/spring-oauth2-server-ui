import { userManager } from './authService';

export function login() {
    userManager.signinRedirect();
}

export function logout() {
    userManager.signoutRedirect();
}