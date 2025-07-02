import React, { useEffect, useState, createContext, useContext } from 'react';
import { userManager } from './authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState(null);

    useEffect(() => {
        userManager.getUser().then((user) => {
            if (user && !user.expired) {
                setUser(user);
            }
        });

        userManager.events.addUserLoaded(setUser);
        userManager.events.addUserUnloaded(() => setUser(null));
    }, []);

    return (
        <AuthContext.Provider value={user}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);