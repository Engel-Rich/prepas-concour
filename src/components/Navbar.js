import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { FaArrowRight, FaBars, FaGraduationCap, FaTimes } from 'react-icons/fa';

const Navbar = () => {
    const [isOpen, setIsOpen] = useState(false);
    const location = useLocation();

    const closeMenu = () => setIsOpen(false);
    const homeAnchor = (anchor) => (location.pathname === '/' ? anchor : `/${anchor}`);

    return (
        <header className="site-header">
            <div className="site-container nav-shell">
                <Link className="brand" to="/" onClick={closeMenu} aria-label="Prépa Concours - Accueil">
                    <span className="brand-mark"><FaGraduationCap /></span>
                    <span className="brand-copy"><strong>Prépa</strong> Concours<small>Réussir ensemble</small></span>
                </Link>

                <button
                    className="nav-toggle"
                    type="button"
                    onClick={() => setIsOpen((open) => !open)}
                    aria-expanded={isOpen}
                    aria-controls="main-navigation"
                    aria-label={isOpen ? 'Fermer le menu' : 'Ouvrir le menu'}
                >
                    {isOpen ? <FaTimes /> : <FaBars />}
                </button>

                <nav id="main-navigation" className={`main-nav${isOpen ? ' is-open' : ''}`} aria-label="Navigation principale">
                    <a href={homeAnchor('#fonctionnalites')} onClick={closeMenu}>Fonctionnalités</a>
                    <a href={homeAnchor('#parcours')} onClick={closeMenu}>Parcours</a>
                    <a href={homeAnchor('#application')} onClick={closeMenu}>Application</a>
                    <Link to="/about" onClick={closeMenu}>À propos</Link>
                    <a href={homeAnchor('#faq')} onClick={closeMenu}>FAQ</a>
                    <a className="nav-cta" href={homeAnchor('#telecharger')} onClick={closeMenu}>Télécharger <FaArrowRight /></a>
                </nav>
            </div>
        </header>
    );
};

export default Navbar;
