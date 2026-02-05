run-db:
	docker run --name prepa-concour-postgres \
		-e POSTGRES_PASSWORD=password \
		-e POSTGRES_USER=postgres \
		-e POSTGRES_DB=prepa_concour \
		-p 5432:5432 \
		-v pgdata:/var/lib/postgresql/data \
		-d postgres:16-alpine
stop-db:
	docker stop prepa-concour-postgres || true
	docker rm prepa-concour-postgres || true