import React from 'react';
import { Container, Row, Col, Card } from 'react-bootstrap';
import { FaGraduationCap, FaBook, FaUniversity, FaChartLine, FaFlask, FaBalanceScale, FaUserMd, FaBuilding } from 'react-icons/fa';

const Home = () => {
    const concours = [
        {
            id: 1,
            title: 'Preparations aux exemuns officiels',
            description: 'Préparation aux concours de la fonction publique',
            icon: <FaGraduationCap />,
        },
        {
            id: 2,
            title: 'Concours Paramédicaux',
            description: 'Formation pour les concours du secteur médical',
            icon: <FaBook />,
        },
        {
            id: 3,
            title: 'Concours Écoles de Commerce',
            description: 'Préparation aux concours des grandes écoles',
            icon: <FaUniversity />,
        },
        {
            id: 4,
            title: 'Concours Écoles d\'Ingénieurs',
            description: 'Formation pour les concours scientifiques',
            icon: <FaChartLine />,
        },
        {
            id: 5,
            title: 'Concours de la Santé',
            description: 'Préparation aux concours médicaux et paramédicaux',
            icon: <FaUserMd />,
        },
        {
            id: 6,
            title: 'Concours Juridiques',
            description: 'Formation pour les concours du droit et de la justice',
            icon: <FaBalanceScale />,
        },
        {
            id: 7,
            title: 'Concours Scientifiques',
            description: 'Préparation aux concours des écoles scientifiques',
            icon: <FaFlask />,
        },
        {
            id: 8,
            title: 'Concours de l\'Architecture',
            description: 'Formation pour les concours des écoles d\'architecture',
            icon: <FaBuilding />,
        },
    ];

    return (
        <div>
            {/* Hero Section */}
            <section className="hero-section text-center">
                <Container>
                    <h1>Monprof </h1>
                    <p className="lead">
                        Une formation complète et personnalisée pour réussir vos examuns et concours, un accompgnement dans les termes de l'art jusqu'a l'atteinte de vos objectifs.
                    </p>
                </Container>
            </section>

            {/* Concours Section */}
            <section className="py-5">
                <Container>
                    <h2 className="text-center section-title">Nos offres</h2>
                    <Row>
                        {concours.map((concour) => (
                            <Col key={concour.id} md={6} lg={3} className="mb-4">
                                <Card className="h-100 text-center">
                                    <Card.Body>
                                        <div className="icon-container">
                                            {concour.icon}
                                        </div>
                                        <Card.Title>{concour.title}</Card.Title>
                                        <Card.Text>{concour.description}</Card.Text>
                                    </Card.Body>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                </Container>
            </section>
        </div>
    );
};

export default Home; 