import React, { useState } from 'react';
import { Container, Row, Col, Form, Button, Alert } from 'react-bootstrap';
import { FaEnvelope, FaPhone, FaMapMarkerAlt } from 'react-icons/fa';

const Contact = () => {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        subject: '',
        message: '',
    });
    const [showAlert, setShowAlert] = useState(false);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        // Ici, vous pouvez ajouter la logique pour envoyer le formulaire
        setShowAlert(true);
        setTimeout(() => setShowAlert(false), 3000);
    };

    return (
        <Container className="py-5">
            <h1 className="text-center section-title mb-5">Contactez-Nous</h1>

            <Row>
                <Col md={6} className="mb-4">
                    <h2>Informations de Contact</h2>
                    <div className="contact-info">
                        <p><FaEnvelope /> contact@prepaconcours.com</p>
                        <p><FaPhone /> +33 1 23 45 67 89</p>
                        <p><FaMapMarkerAlt /> 123 Rue de la Formation, 75000 Paris</p>
                    </div>
                </Col>

                <Col md={6}>
                    <Form onSubmit={handleSubmit}>
                        {showAlert && (
                            <Alert variant="success" onClose={() => setShowAlert(false)} dismissible>
                                Votre message a été envoyé avec succès !
                            </Alert>
                        )}

                        <Form.Group className="mb-3">
                            <Form.Label>Nom</Form.Label>
                            <Form.Control
                                type="text"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Email</Form.Label>
                            <Form.Control
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Sujet</Form.Label>
                            <Form.Control
                                type="text"
                                name="subject"
                                value={formData.subject}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Message</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={4}
                                name="message"
                                value={formData.message}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>

                        <Button variant="primary" type="submit">
                            Envoyer
                        </Button>
                    </Form>
                </Col>
            </Row>
        </Container>
    );
};

export default Contact; 