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

open-bucket:
	docker run \
		-p 9000:9000 \
		-p 9001:9001 \
		--name minio_server \
		-v minio_data:/data \
		-e MINIO_ROOT_USER=prepa-db-root \
		-e MINIO_ROOT_PASSWORD=prepa-db-pass \
		-d quay.io/minio/minio \
		server --console-address ":9001" /data

close-bucket:
	docker stop minio_server || true
	docker rm minio_server || true
