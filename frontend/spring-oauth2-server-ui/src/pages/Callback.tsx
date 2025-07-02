import { useEffect } from 'react';
import { userManager } from '../auth/authService';
import { useNavigate } from 'react-router-dom';

const Callback = () => {
    const navigate = useNavigate();

    useEffect(() => {
        userManager.signinRedirectCallback().then(() => {
            navigate('/');
        });
    }, [navigate]);

    return <div>Signing in...</div>;
};

export default Callback;