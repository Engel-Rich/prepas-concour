import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import About from './pages/About';
import Contact from './pages/Contact';
import PrivacyPolicy from './pages/PrivacyPolicy';
import TermsOfService from './pages/TermsOfService';
import Footer from './components/Footer';

const ScrollManager = () => {
    const { pathname, hash } = useLocation();

    useEffect(() => {
        if (hash) {
            window.requestAnimationFrame(() => {
                document.querySelector(hash)?.scrollIntoView({ behavior: 'smooth' });
            });
            return;
        }

        window.scrollTo({ top: 0, behavior: 'auto' });
    }, [pathname, hash]);

    return null;
};

function App() {
    return (
        <Router>
            <div className="App">
                <ScrollManager />
                <Navbar />
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/about" element={<About />} />
                    <Route path="/contact" element={<Contact />} />
                    <Route path="/privacy-policy" element={<PrivacyPolicy />} />
                    <Route path="/terms-of-service" element={<TermsOfService />} />
                </Routes>
                <Footer />
            </div>
        </Router>
    );
}

export default App;
