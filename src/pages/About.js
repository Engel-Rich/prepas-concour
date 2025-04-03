import React from 'react';
import { Container, Row, Col, Card } from 'react-bootstrap';
import { FaBullseye, FaUsers, FaChartBar } from 'react-icons/fa';

const About = () => {
    const features = [
        {
            id: 1,
            title: 'Notre Mission',
            description: 'Offrir une préparation de qualité pour aider les candidats à réussir leurs concours.',
            icon: <FaBullseye />,
        },
        {
            id: 2,
            title: 'Notre Expertise',
            description: 'Une équipe de formateurs expérimentés et spécialisés dans différents domaines.',
            icon: <FaUsers />,
        },
        {
            id: 3,
            title: 'Nos Résultats',
            description: 'Un taux de réussite élevé grâce à notre méthode pédagogique éprouvée.',
            icon: <FaChartBar />,
        },
    ];

    return (
        <Container className="py-5">
            <h1 className="text-center section-title mb-5">À Propos de Nous</h1>

            <Row className="mb-5">
                <Col md={6}>
                    <h2>Qui Sommes-Nous ?</h2>
                    <p>
                        Prépa Concours est un centre de formation spécialisé dans la préparation aux concours.
                        Depuis notre création, nous accompagnons les candidats dans leur parcours de réussite
                        avec une approche personnalisée et des méthodes pédagogiques innovantes.
                    </p>
                    <p>
                        Notre équipe est composée de formateurs expérimentés, anciens membres de jurys
                        et spécialistes dans leurs domaines respectifs.
                    </p>
                </Col>
                <Col md={6}>
                    <h2>Notre Approche</h2>
                    <p>
                        Nous croyons en une formation complète qui combine :
                    </p>
                    <ul>
                        <li>Un suivi personnalisé de chaque candidat</li>
                        <li>Des cours de qualité dispensés par des experts</li>
                        <li>Des entraînements réguliers aux épreuves</li>
                        <li>Un accompagnement méthodologique</li>
                    </ul>
                </Col>
            </Row>

            <Row className="mt-4">
                {features.map((feature) => (
                    <Col key={feature.id} md={4}>
                        <Card className="h-100 text-center">
                            <Card.Body>
                                <div className="icon-container">
                                    {feature.icon}
                                </div>
                                <Card.Title>{feature.title}</Card.Title>
                                <Card.Text>{feature.description}</Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>
        </Container>
    );
};

export default About; 