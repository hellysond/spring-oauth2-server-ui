import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider, useAuth } from './auth/AuthProvider';
import Callback from './pages/Callback';
import { login, logout } from './auth/authActions';

function HomePage() {
    const user = useAuth();

    return (
        <div>
            {user ? (
                <>
                    <p>Hello, {user.profile.name}</p>
                    <button onClick={logout}>Logout</button>
                </>
            ) : (
                <button onClick={login}>Login</button>
            )}
        </div>
    );
}

export default function App() {
    return (
        <Router>
            <AuthProvider>
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/callback" element={<Callback />} />
                </Routes>
            </AuthProvider>
        </Router>
    );
}