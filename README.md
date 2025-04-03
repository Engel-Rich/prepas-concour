# Prépa Concours - Site Web

## Description
Prépa Concours est une plateforme web dédiée à la préparation des concours officiels. Notre site offre une interface moderne et intuitive pour accéder aux différentes formations et ressources pédagogiques.

## Fonctionnalités
- Présentation des différents concours disponibles
- Page d'information sur notre approche pédagogique
- Formulaire de contact
- Interface responsive et moderne
- Application mobile disponible sur App Store et Google Play

## Technologies Utilisées
- React.js
- Bootstrap 5
- Node.js
- Docker
- Nginx

## Prérequis
- Node.js (v14 ou supérieur)
- npm ou yarn
- Docker

## Installation

### Développement Local
```bash
# Cloner le repository
git clone [url-du-repo]
cd prepa-concour-site

# Installer les dépendances
npm install

# Lancer l'application en mode développement
npm start
```

### Avec Docker
Le projet inclut un Dockerfile et un fichier Makefile pour faciliter le déploiement.

```bash
# Construire et lancer avec Make
make

# Ou étape par étape
make build  # Construire l'image
make run    # Lancer le conteneur
```

### Commandes Make Disponibles
- `make build` : Construit l'image Docker
- `make run` : Lance le conteneur (port 8086)
- `make stop` : Arrête le conteneur
- `make restart` : Redémarre le conteneur
- `make clean` : Nettoie l'installation
- `make logs` : Affiche les logs
- `make status` : Affiche le statut du conteneur

## Structure du Projet
```
prepa-concour-site/
├── src/
│   ├── components/     # Composants React réutilisables
│   ├── pages/         # Pages principales
│   └── App.js         # Point d'entrée de l'application
├── public/            # Fichiers statiques
├── Dockerfile         # Configuration Docker
├── nginx.conf         # Configuration Nginx
└── Makefile          # Scripts de build et déploiement
```

## Pages Disponibles
- `/` : Page d'accueil avec la liste des concours
- `/about` : À propos de nous
- `/contact` : Formulaire de contact
- `/privacy-policy` : Politique de confidentialité
- `/terms-of-service` : Conditions d'utilisation

## Personnalisation
- La couleur principale du site peut être modifiée dans `src/App.css` (actuellement #6667AB)
- Les icônes peuvent être remplacées dans le dossier `public/`
- Le contenu des pages peut être modifié dans les fichiers correspondants dans `src/pages/`

## Déploiement
Le site est configuré pour être déployé via Docker avec Nginx comme serveur web.
Le conteneur inclut :
- Compression gzip
- Cache optimisé
- En-têtes de sécurité
- Redémarrage automatique

## Contribution
Les contributions sont les bienvenues ! N'hésitez pas à ouvrir une issue ou une pull request.

## Licence
[Votre licence]

## Contact
Pour toute question ou suggestion, contactez-nous à [votre-email] 