import React, { useState } from 'react';
import { FaArrowRight, FaEnvelope, FaHeadset, FaMobileAlt } from 'react-icons/fa';

const CONTACT_EMAIL = 'contact@mutrix.org';

const Contact = () => {
    const [formData, setFormData] = useState({ name: '', email: '', subject: '', message: '' });

    const handleChange = ({ target }) => {
        setFormData((current) => ({ ...current, [target.name]: target.value }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        const subject = encodeURIComponent(`[Prépa Concours] ${formData.subject}`);
        const body = encodeURIComponent(`Nom : ${formData.name}\nEmail : ${formData.email}\n\n${formData.message}`);
        window.location.href = `mailto:${CONTACT_EMAIL}?subject=${subject}&body=${body}`;
    };

    return (
        <main className="inner-page contact-page">
            <section className="inner-hero inner-hero--compact">
                <div className="site-container section-heading section-heading--center section-heading--light">
                    <div className="eyebrow eyebrow--light"><span /> Nous contacter</div>
                    <h1>Une question ? Parlons-en.</h1>
                    <p>Compte, abonnement, paiement ou utilisation de l’application : décrivez-nous votre besoin et nous vous répondrons au mieux.</p>
                </div>
            </section>

            <section className="section-block contact-content">
                <div className="site-container contact-grid">
                    <div className="contact-aside">
                        <div className="eyebrow"><span /> Assistance</div>
                        <h2>Nous sommes là pour vous aider.</h2>
                        <p>Pour une réponse plus efficace, indiquez l’adresse e-mail liée à votre compte et décrivez précisément le problème rencontré.</p>
                        <div className="contact-card">
                            <span><FaEnvelope /></span>
                            <div><small>E-mail</small><a href={`mailto:${CONTACT_EMAIL}`}>{CONTACT_EMAIL}</a></div>
                        </div>
                        <div className="contact-card">
                            <span><FaHeadset /></span>
                            <div><small>Support</small><strong>Application et abonnements</strong></div>
                        </div>
                        <div className="contact-card">
                            <span><FaMobileAlt /></span>
                            <div><small>Conseil</small><strong>Ajoutez une capture si nécessaire</strong></div>
                        </div>
                    </div>

                    <form className="contact-form" onSubmit={handleSubmit}>
                        <div className="form-heading">
                            <span>Envoyer une demande</span>
                            <h2>Comment pouvons-nous vous aider ?</h2>
                        </div>
                        <div className="form-row">
                            <label>Votre nom<input type="text" name="name" value={formData.name} onChange={handleChange} placeholder="Nom complet" required /></label>
                            <label>Votre e-mail<input type="email" name="email" value={formData.email} onChange={handleChange} placeholder="vous@exemple.com" required /></label>
                        </div>
                        <label>Sujet<input type="text" name="subject" value={formData.subject} onChange={handleChange} placeholder="Ex. : problème d’accès à mon cours" required /></label>
                        <label>Votre message<textarea name="message" value={formData.message} onChange={handleChange} rows="6" placeholder="Décrivez votre demande avec le plus de détails possible…" required /></label>
                        <p className="form-note">Le bouton ouvrira votre application de messagerie avec le message préparé.</p>
                        <button className="primary-button" type="submit">Préparer mon e-mail <FaArrowRight /></button>
                    </form>
                </div>
            </section>
        </main>
    );
};

export default Contact;
