import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { FaFacebook, FaTwitter, FaLinkedin, FaInstagram, FaApple, FaGooglePlay } from 'react-icons/fa';

const Footer = () => {
    return (
        <footer className="mt-5">
            <Container>
                <Row>
                    <Col md={4} className="mb-4">
                        <h5>Prépa Concours</h5>
                        <p>
                            Votre partenaire de confiance pour la préparation aux concours.
                            Une formation de qualité pour votre réussite.
                        </p>
                        <div className="app-download-buttons mt-3">
                            <a href="#" className="btn btn-dark me-2 mb-2">
                                <FaApple className="me-2" />
                                App Store
                            </a>
                            <a href="#" className="btn btn-dark mb-2">
                                <FaGooglePlay className="me-2" />
                                Google Play
                            </a>
                        </div>
                    </Col>

                    <Col md={4} className="mb-4">
                        <h5>Liens Rapides</h5>
                        <ul className="list-unstyled">
                            <li><Link to="/" className="text-white">Accueil</Link></li>
                            <li><Link to="/about" className="text-white">À propos</Link></li>
                            <li><Link to="/contact" className="text-white">Contact</Link></li>
                        </ul>
                    </Col>

                    <Col md={4} className="mb-4">
                        <h5>Légal</h5>
                        <ul className="list-unstyled">
                            <li><Link to="/privacy-policy" className="text-white">Politique de Confidentialité</Link></li>
                            <li><Link to="/terms-of-service" className="text-white">Conditions d'Utilisation</Link></li>
                        </ul>
                    </Col>
                </Row>

                <Row className="mt-4">
                    <Col className="text-center">
                        <div className="social-icons">
                            <a href="#" className="text-white me-3"><FaFacebook /></a>
                            <a href="#" className="text-white me-3"><FaTwitter /></a>
                            <a href="#" className="text-white me-3"><FaLinkedin /></a>
                            <a href="#" className="text-white"><FaInstagram /></a>
                        </div>
                        <p className="mt-3 mb-0">
                            &copy; {new Date().getFullYear()} Prépa Concours. Tous droits réservés.
                        </p>
                    </Col>
                </Row>
            </Container>
        </footer>
    );
};

export default Footer; 