import React from 'react';
import { Navbar as BootstrapNavbar, Nav, Container } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { FaGraduationCap } from 'react-icons/fa';

const Navbar = () => {
    return (
        <BootstrapNavbar expand="lg" variant="dark">
            <Container>
                <BootstrapNavbar.Brand as={Link} to="/">
                    <FaGraduationCap className="me-2" style={{ fontSize: '1.5rem' }} />
                    Prépa Concours
                </BootstrapNavbar.Brand>
                <BootstrapNavbar.Toggle aria-controls="basic-navbar-nav" />
                <BootstrapNavbar.Collapse id="basic-navbar-nav">
                    <Nav className="ms-auto">
                        <Nav.Link as={Link} to="/">Accueil</Nav.Link>
                        <Nav.Link as={Link} to="/about">À propos</Nav.Link>
                        <Nav.Link as={Link} to="/contact">Contact</Nav.Link>
                    </Nav>
                </BootstrapNavbar.Collapse>
            </Container>
        </BootstrapNavbar>
    );
};

export default Navbar; 