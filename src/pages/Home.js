import React from 'react';
import {
    FaApple,
    FaArrowRight,
    FaBookOpen,
    FaCheck,
    FaClock,
    FaDownload,
    FaGooglePlay,
    FaGraduationCap,
    FaHistory,
    FaLock,
    FaMobileAlt,
    FaPlay,
    FaTicketAlt,
    FaWallet,
} from 'react-icons/fa';
import { APP_STORE_URL, PLAY_STORE_URL } from '../config/links';

const StoreButtons = ({ compact = false }) => (
    <div className={`store-buttons${compact ? ' store-buttons--compact' : ''}`}>
        <a className="store-button" href={APP_STORE_URL} target="_blank" rel="noreferrer" aria-label="Télécharger Prépa Concours sur l’App Store">
            <FaApple aria-hidden="true" />
            <span><small>Télécharger sur</small>App Store</span>
        </a>
        <a className="store-button store-button--light" href={PLAY_STORE_URL} target="_blank" rel="noreferrer" aria-label="Télécharger Prépa Concours sur Google Play">
            <FaGooglePlay aria-hidden="true" />
            <span><small>Disponible sur</small>Google Play</span>
        </a>
    </div>
);

const Phone = ({ src, alt, className = '' }) => (
    <div className={`phone-frame ${className}`}>
        <div className="phone-speaker" aria-hidden="true" />
        <img src={src} alt={alt} loading="lazy" />
    </div>
);

const Home = () => {
    const features = [
        {
            icon: <FaBookOpen />,
            title: 'Des cours bien structurés',
            text: 'Avancez matière par matière avec des contenus clairs, des conseils pratiques et des ressources à consulter à votre rythme.',
        },
        {
            icon: <FaPlay />,
            title: 'Vidéos et supports utiles',
            text: 'Retrouvez vos leçons, vidéos et documents au même endroit pour réviser sans vous disperser.',
        },
        {
            icon: <FaTicketAlt />,
            title: 'Un accès simple et sécurisé',
            text: 'Activez votre préparation avec un abonnement ou un code d’accès, puis partagez les places disponibles avec vos proches.',
        },
        {
            icon: <FaWallet />,
            title: 'Paiement Mobile Money',
            text: 'Réglez votre abonnement depuis l’application avec les moyens de paiement disponibles, en toute transparence.',
        },
    ];

    const steps = [
        {
            number: '01',
            title: 'Téléchargez l’application',
            text: 'Installez Prépa Concours sur votre téléphone et créez votre espace personnel.',
        },
        {
            number: '02',
            title: 'Choisissez votre concours',
            text: 'Sélectionnez la session qui vous intéresse et découvrez le parcours de préparation.',
        },
        {
            number: '03',
            title: 'Préparez-vous à votre rythme',
            text: 'Consultez vos cours, révisez régulièrement et gardez toutes vos ressources à portée de main.',
        },
    ];

    const faqs = [
        {
            question: 'À qui s’adresse Prépa Concours ?',
            answer: 'L’application s’adresse aux candidats qui souhaitent organiser leur préparation aux concours et retrouver leurs cours, vidéos et ressources dans un espace unique.',
        },
        {
            question: 'Comment accéder aux contenus premium ?',
            answer: 'Choisissez un abonnement dans l’application, sélectionnez votre moyen de paiement puis suivez les instructions. Un code d’accès peut également vous être attribué selon l’offre choisie.',
        },
        {
            question: 'Puis-je consulter mes cours sur mobile ?',
            answer: 'Oui. Prépa Concours est pensée d’abord pour le mobile afin que vous puissiez réviser où que vous soyez, dès que vous avez un moment.',
        },
        {
            question: 'Comment contacter l’assistance ?',
            answer: 'Utilisez la page Contact du site. Notre équipe pourra vous aider pour l’accès à votre compte, votre abonnement ou l’utilisation de l’application.',
        },
    ];

    return (
        <main>
            <section className="hero" id="accueil">
                <div className="hero-orb hero-orb--one" aria-hidden="true" />
                <div className="hero-orb hero-orb--two" aria-hidden="true" />
                <div className="site-container hero-grid">
                    <div className="hero-copy">
                        <div className="eyebrow"><span /> Votre réussite commence ici</div>
                        <h1>Préparez votre concours avec une méthode <em>claire et accessible.</em></h1>
                        <p className="hero-lead">
                            Cours, vidéos, ressources et abonnements : retrouvez l’essentiel de votre préparation dans une seule application, où que vous soyez.
                        </p>
                        <StoreButtons />
                        <div className="hero-proof" aria-label="Avantages de l’application">
                            <span><FaCheck /> Contenus organisés</span>
                            <span><FaCheck /> Paiement Mobile Money</span>
                            <span><FaCheck /> Accès sécurisé</span>
                        </div>
                    </div>

                    <div className="hero-visual" aria-label="Aperçu de l’application Prépa Concours">
                        <div className="hero-badge hero-badge--top">
                            <span><FaBookOpen /></span>
                            <div><strong>Vos cours</strong><small>toujours à portée de main</small></div>
                        </div>
                        <Phone src="/images/app/course-list.png" alt="Liste des cours dans l’application Prépa Concours" className="phone-frame--hero-main" />
                        <Phone src="/images/app/subscription.png" alt="Écran d’abonnement dans l’application Prépa Concours" className="phone-frame--hero-back" />
                        <div className="hero-badge hero-badge--bottom">
                            <span><FaLock /></span>
                            <div><strong>Accès protégé</strong><small>simple et sécurisé</small></div>
                        </div>
                    </div>
                </div>
            </section>

            <section className="trust-strip" aria-label="Points forts">
                <div className="site-container trust-grid">
                    <div><FaBookOpen /><span><strong>Apprentissage guidé</strong><small>Un parcours clair</small></span></div>
                    <div><FaClock /><span><strong>À votre rythme</strong><small>Partout, à tout moment</small></span></div>
                    <div><FaMobileAlt /><span><strong>Conçu pour le mobile</strong><small>Une expérience fluide</small></span></div>
                    <div><FaLock /><span><strong>Accès sécurisé</strong><small>Vos contenus protégés</small></span></div>
                </div>
            </section>

            <section className="section-block features-section" id="fonctionnalites">
                <div className="site-container">
                    <div className="section-heading section-heading--center">
                        <div className="eyebrow"><span /> Tout pour bien vous préparer</div>
                        <h2>Une préparation plus simple, du premier cours jusqu’au jour J.</h2>
                        <p>Chaque fonctionnalité vous aide à rester concentré sur ce qui compte : comprendre, réviser et progresser.</p>
                    </div>
                    <div className="feature-grid">
                        {features.map((feature) => (
                            <article className="feature-card" key={feature.title}>
                                <div className="feature-icon">{feature.icon}</div>
                                <h3>{feature.title}</h3>
                                <p>{feature.text}</p>
                                <a href="#application">Découvrir l’application <FaArrowRight /></a>
                            </article>
                        ))}
                    </div>
                </div>
            </section>

            <section className="section-block product-section" id="parcours">
                <div className="site-container product-grid">
                    <div className="product-visual">
                        <div className="product-panel" aria-hidden="true" />
                        <Phone src="/images/app/course-detail.png" alt="Détail d’un cours de préparation à la dictée" className="phone-frame--product" />
                        <div className="floating-note">
                            <span><FaDownload /></span>
                            <div><strong>Ressources disponibles</strong><small>Consultez vos supports de cours</small></div>
                        </div>
                    </div>
                    <div className="product-copy">
                        <div className="eyebrow"><span /> Un parcours qui vous ressemble</div>
                        <h2>Révisez l’essentiel, sans vous perdre.</h2>
                        <p>
                            L’application rassemble vos matières, vos cours et vos ressources dans une interface lisible. Vous savez quoi travailler et retrouvez facilement chaque contenu.
                        </p>
                        <ul className="check-list">
                            <li><FaCheck /><span><strong>Des matières bien organisées</strong><small>Accédez rapidement au cours dont vous avez besoin.</small></span></li>
                            <li><FaCheck /><span><strong>Des contenus détaillés</strong><small>Lisez les consignes et les méthodes directement dans l’app.</small></span></li>
                            <li><FaCheck /><span><strong>Des ressources disponibles sur mobile</strong><small>Gardez vos supports de préparation avec vous.</small></span></li>
                        </ul>
                        <a className="text-link" href="#telecharger">Commencer ma préparation <FaArrowRight /></a>
                    </div>
                </div>
            </section>

            <section className="section-block app-showcase" id="application">
                <div className="site-container">
                    <div className="section-heading section-heading--center section-heading--light">
                        <div className="eyebrow eyebrow--light"><span /> Une application complète</div>
                        <h2>Tout votre parcours dans une seule app.</h2>
                        <p>De l’activation de votre accès au suivi de vos paiements, chaque étape reste simple et lisible.</p>
                    </div>
                    <div className="showcase-grid">
                        <div className="showcase-item showcase-item--one">
                            <Phone src="/images/app/course-list.png" alt="Cours disponibles dans Prépa Concours" />
                            <div><span>01</span><strong>Choisissez votre cours</strong></div>
                        </div>
                        <div className="showcase-item showcase-item--two">
                            <Phone src="/images/app/subscription.png" alt="Paiement d’un abonnement dans Prépa Concours" />
                            <div><span>02</span><strong>Activez votre abonnement</strong></div>
                        </div>
                        <div className="showcase-item showcase-item--three">
                            <Phone src="/images/app/access-codes.png" alt="Gestion des codes d’accès Prépa Concours" />
                            <div><span>03</span><strong>Gérez vos accès</strong></div>
                        </div>
                        <div className="showcase-item showcase-item--four">
                            <Phone src="/images/app/transactions.png" alt="Historique des transactions Prépa Concours" />
                            <div><span>04</span><strong>Suivez vos paiements</strong></div>
                        </div>
                    </div>
                </div>
            </section>

            <section className="section-block steps-section">
                <div className="site-container">
                    <div className="section-heading section-heading--center">
                        <div className="eyebrow"><span /> Simple dès le départ</div>
                        <h2>Votre préparation commence en trois étapes.</h2>
                    </div>
                    <div className="steps-grid">
                        {steps.map((step, index) => (
                            <article className="step-card" key={step.number}>
                                <div className="step-number">{step.number}</div>
                                <div className="step-icon">
                                    {index === 0 ? <FaDownload /> : index === 1 ? <FaGraduationCap /> : <FaBookOpen />}
                                </div>
                                <h3>{step.title}</h3>
                                <p>{step.text}</p>
                            </article>
                        ))}
                    </div>
                </div>
            </section>

            <section className="download-section" id="telecharger">
                <div className="site-container download-grid">
                    <div className="download-copy">
                        <div className="eyebrow eyebrow--light"><span /> Prêt à commencer ?</div>
                        <h2>Emportez votre préparation partout avec vous.</h2>
                        <p>Téléchargez Prépa Concours et transformez chaque moment disponible en occasion d’avancer.</p>
                        <StoreButtons />
                    </div>
                    <div className="download-visual" aria-label="Écran de démarrage de Prépa Concours">
                        <Phone src="/images/app/splash.png" alt="Écran de démarrage rouge de l’application Prépa Concours" className="phone-frame--download" />
                        <div className="download-chip download-chip--one"><FaHistory /> Progression flexible</div>
                        <div className="download-chip download-chip--two"><FaLock /> Accès sécurisé</div>
                    </div>
                </div>
            </section>

            <section className="section-block faq-section" id="faq">
                <div className="site-container faq-grid">
                    <div className="section-heading">
                        <div className="eyebrow"><span /> Questions fréquentes</div>
                        <h2>Les réponses pour démarrer sereinement.</h2>
                        <p>Vous avez encore une question ? Notre équipe reste disponible pour vous accompagner.</p>
                        <a className="secondary-button" href="/contact">Nous contacter <FaArrowRight /></a>
                    </div>
                    <div className="faq-list">
                        {faqs.map((faq, index) => (
                            <details key={faq.question} open={index === 0}>
                                <summary>{faq.question}<span>+</span></summary>
                                <p>{faq.answer}</p>
                            </details>
                        ))}
                    </div>
                </div>
            </section>
        </main>
    );
};

export { StoreButtons };
export default Home;
