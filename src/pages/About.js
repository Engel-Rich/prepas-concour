import React from 'react';
import { Link } from 'react-router-dom';
import { FaArrowRight, FaBookOpen, FaBullseye, FaMobileAlt, FaUsers } from 'react-icons/fa';

const About = () => (
    <main className="inner-page">
        <section className="inner-hero">
            <div className="site-container inner-hero-grid">
                <div>
                    <div className="eyebrow eyebrow--light"><span /> À propos</div>
                    <h1>Aider chaque candidat à préparer son concours avec plus de clarté.</h1>
                    <p>Prépa Concours est une solution numérique développée par Mutrix pour rendre les contenus de préparation plus accessibles, mieux organisés et plus simples à consulter sur mobile.</p>
                </div>
                <div className="inner-hero-card">
                    <span><FaBullseye /></span>
                    <p>Notre cap</p>
                    <strong>Transformer le temps disponible en progrès concret.</strong>
                </div>
            </div>
        </section>

        <section className="section-block">
            <div className="site-container about-intro-grid">
                <div className="section-heading">
                    <div className="eyebrow"><span /> Notre démarche</div>
                    <h2>Une expérience pensée autour des besoins réels des candidats.</h2>
                </div>
                <div className="about-copy">
                    <p>Préparer un concours demande de la régularité, une bonne méthode et des ressources faciles à retrouver. L’application réunit ces éléments dans un espace mobile cohérent.</p>
                    <p>Notre rôle est de faciliter l’accès aux parcours, aux cours, aux supports et aux abonnements, afin que chaque candidat puisse se concentrer sur sa préparation.</p>
                </div>
            </div>

            <div className="site-container value-grid">
                <article className="value-card">
                    <span><FaBookOpen /></span>
                    <h3>Clarté</h3>
                    <p>Des contenus structurés pour comprendre rapidement quoi travailler.</p>
                </article>
                <article className="value-card">
                    <span><FaMobileAlt /></span>
                    <h3>Accessibilité</h3>
                    <p>Une expérience mobile pour réviser dès qu’un moment se présente.</p>
                </article>
                <article className="value-card">
                    <span><FaUsers /></span>
                    <h3>Accompagnement</h3>
                    <p>Un parcours plus lisible et une équipe disponible en cas de besoin.</p>
                </article>
            </div>
        </section>

        <section className="simple-cta">
            <div className="site-container simple-cta-inner">
                <div><span>Votre prochain objectif commence ici.</span><h2>Découvrez l’application Prépa Concours.</h2></div>
                <Link className="primary-button" to="/#telecharger">Télécharger l’application <FaArrowRight /></Link>
            </div>
        </section>
    </main>
);

export default About;
