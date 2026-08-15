import React from 'react';
import { Link } from 'react-router-dom';
import { FaApple, FaEnvelope, FaGooglePlay, FaGraduationCap } from 'react-icons/fa';

const Footer = () => (
    <footer className="site-footer">
        <div className="site-container footer-grid">
            <div className="footer-brand">
                <Link className="brand brand--footer" to="/">
                    <span className="brand-mark"><FaGraduationCap /></span>
                    <span className="brand-copy"><strong>Prépa</strong> Concours<small>Réussir ensemble</small></span>
                </Link>
                <p>Une application pensée pour vous aider à organiser vos révisions et avancer avec confiance vers votre concours.</p>
                <a className="footer-email" href="mailto:contact@mutrix.org"><FaEnvelope /> contact@mutrix.org</a>
            </div>

            <div className="footer-column">
                <h2>Découvrir</h2>
                <Link to="/#fonctionnalites">Fonctionnalités</Link>
                <Link to="/#parcours">Votre parcours</Link>
                <Link to="/#application">L’application</Link>
                <Link to="/#telecharger">Télécharger</Link>
            </div>

            <div className="footer-column">
                <h2>Prépa Concours</h2>
                <Link to="/about">À propos</Link>
                <Link to="/contact">Contact</Link>
                <Link to="/privacy-policy">Confidentialité</Link>
                <Link to="/terms-of-service">Conditions d’utilisation</Link>
            </div>

            <div className="footer-column footer-download">
                <h2>Télécharger l’app</h2>
                <p>Commencez votre préparation depuis votre téléphone.</p>
                <a href="https://apps.apple.com/ca/app/monprof-mutrix/id6753905724" target="_blank" rel="noreferrer"><FaApple /> App Store</a>
                <a href="https://play.google.com/store/apps/details?id=mutrix.app.concours" target="_blank" rel="noreferrer"><FaGooglePlay /> Google Play</a>
            </div>
        </div>

        <div className="site-container footer-bottom">
            <p>© {new Date().getFullYear()} Prépa Concours. Tous droits réservés.</p>
            <p>Une solution Mutrix</p>
        </div>
    </footer>
);

export default Footer;
