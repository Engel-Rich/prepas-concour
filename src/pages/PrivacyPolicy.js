import React from 'react';
import { Container } from 'react-bootstrap';

const PrivacyPolicy = () => {
    return (
        <main className="legal-page">
        <Container className="py-5 legal-content">
            <p className="legal-kicker">Informations légales</p>
            <h1>Politique de confidentialité</h1>
            <p className="legal-intro">Dernière mise à jour : 16 août 2026</p>

            <section className="mb-4">
                <h2>1. Introduction</h2>
                <p>
                    Prépa Concours s’engage à protéger votre vie privée. Cette politique de confidentialité
                    explique comment nous collectons, utilisons et protégeons vos informations personnelles.
                </p>
            </section>

            <section className="mb-4">
                <h2>2. Collecte des Informations</h2>
                <p>Nous collectons les informations suivantes :</p>
                <ul>
                    <li>Informations d'identification (nom, prénom, email)</li>
                    <li>Informations de contact (adresse, numéro de téléphone)</li>
                    <li>Informations académiques (niveau d'études, concours visés)</li>
                    <li>Données de navigation sur notre site</li>
                </ul>
            </section>

            <section className="mb-4">
                <h2>3. Utilisation des Informations</h2>
                <p>Nous utilisons vos informations pour :</p>
                <ul>
                    <li>Vous fournir nos services de préparation aux concours</li>
                    <li>Vous contacter concernant nos formations</li>
                    <li>Améliorer nos services</li>
                    <li>Respecter nos obligations légales</li>
                </ul>
            </section>

            <section className="mb-4">
                <h2>4. Protection des Données</h2>
                <p>
                    Nous mettons en œuvre des mesures de sécurité appropriées pour protéger vos informations
                    personnelles contre tout accès, modification, divulgation ou destruction non autorisés.
                </p>
            </section>

            <section className="mb-4">
                <h2>5. Vos Droits</h2>
                <p>Conformément au RGPD, vous disposez des droits suivants :</p>
                <ul>
                    <li>Droit d'accès à vos données</li>
                    <li>Droit de rectification</li>
                    <li>Droit à l'effacement</li>
                    <li>Droit à la limitation du traitement</li>
                    <li>Droit à la portabilité des données</li>
                    <li>Droit d'opposition</li>
                </ul>
            </section>

            <section className="mb-4">
                <h2>6. Cookies</h2>
                <p>
                    Notre site utilise des cookies pour améliorer votre expérience de navigation.
                    Vous pouvez configurer votre navigateur pour refuser les cookies.
                </p>
            </section>

            <section className="mb-4">
                <h2>7. Contact</h2>
                <p>
                    Pour toute question concernant cette politique de confidentialité,
                    veuillez nous contacter à l’adresse : contact@mutrix.org
                </p>
            </section>
        </Container>
        </main>
    );
};

export default PrivacyPolicy;
