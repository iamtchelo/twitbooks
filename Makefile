up:
	docker-compose up -d
reset:
	docker-compose stop
	docker-compose rm -f postgres
	docker-compose up -d
destroy:
	docker-compose stop
	docker-compose rm -f postgres
