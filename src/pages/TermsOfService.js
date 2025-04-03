import React from 'react';
import { Container } from 'react-bootstrap';

const TermsOfService = () => {
    return (
        <Container className="py-5">
            <h1 className="text-center section-title mb-5">Conditions d'Utilisation</h1>

            <section className="mb-4">
                <h2>1. Acceptation des Conditions</h2>
                <p>
                    En accédant et en utilisant le site web de Prépa Concours, vous acceptez d'être lié
                    par les présentes conditions d'utilisation. Si vous n'acceptez pas ces conditions,
                    veuillez ne pas utiliser notre site.
                </p>
            </section>

            <section className="mb-4">
                <h2>2. Services Proposés</h2>
                <p>
                    Prépa Concours propose des services de préparation aux concours, notamment :
                </p>
                <ul>
                    <li>Formations en présentiel et en ligne</li>
                    <li>Accompagnement personnalisé</li>
                    <li>Ressources pédagogiques</li>
                    <li>Simulations d'épreuves</li>
                </ul>
            </section>

            <section className="mb-4">
                <h2>3. Inscription et Paiement</h2>
                <p>
                    L'inscription à nos formations nécessite :
                </p>
                <ul>
                    <li>Le paiement des frais de formation selon les tarifs en vigueur</li>
                    <li>L'acceptation des conditions générales de vente</li>
                    <li>La fourniture d'informations exactes et complètes</li>
                </ul>
            </section>

            <section className="mb-4">
                <h2>4. Propriété Intellectuelle</h2>
                <p>
                    Tout le contenu du site (textes, images, vidéos, etc.) est la propriété exclusive
                    de Prépa Concours et est protégé par les lois sur la propriété intellectuelle.
                </p>
            </section>

            <section className="mb-4">
                <h2>5. Responsabilité</h2>
                <p>
                    Prépa Concours s'efforce de fournir des informations exactes et à jour,
                    mais ne peut garantir l'exactitude ou l'exhaustivité des informations
                    présentes sur le site.
                </p>
            </section>

            <section className="mb-4">
                <h2>6. Modification des Conditions</h2>
                <p>
                    Nous nous réservons le droit de modifier ces conditions à tout moment.
                    Les modifications prendront effet dès leur publication sur le site.
                </p>
            </section>

            <section className="mb-4">
                <h2>7. Résiliation</h2>
                <p>
                    Prépa Concours se réserve le droit de résilier ou de suspendre l'accès
                    à ses services en cas de non-respect des présentes conditions.
                </p>
            </section>

            <section className="mb-4">
                <h2>8. Contact</h2>
                <p>
                    Pour toute question concernant ces conditions d'utilisation,
                    veuillez nous contacter à l'adresse : legal@prepaconcours.com
                </p>
            </section>
        </Container>
    );
};

export default TermsOfService; 