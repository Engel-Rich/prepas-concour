# Variables
IMAGE_NAME = prepa-concours
CONTAINER_NAME = prepa-concours-app
PORT = 8086

# Commandes
.PHONY: build run stop restart clean

# Construire l'image Docker
build:
	docker build -t $(IMAGE_NAME) .

# Exécuter le conteneur avec redémarrage automatique
run:
	docker run -d \
		--name $(CONTAINER_NAME) \
		--restart always \
		-p $(PORT):80 \
		$(IMAGE_NAME)

# Arrêter le conteneur
stop:
	docker stop $(CONTAINER_NAME)
	docker rm $(CONTAINER_NAME)

# Redémarrer le conteneur
restart: stop run

# Nettoyer (arrêter le conteneur et supprimer l'image)
clean: stop
	docker rmi $(IMAGE_NAME)

# Construire et exécuter (commande par défaut)
all: build run

# Afficher les logs du conteneur
logs:
	docker logs -f $(CONTAINER_NAME)

# Afficher le statut du conteneur
status:
	docker ps -f name=$(CONTAINER_NAME) 