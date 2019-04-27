up:
	docker-compose up -d
reset:
	docker-compose stop
	docker-compose rm postgres
	docker-compose up -d
destroy:
	docker-compose stop
	docker-compose rm -f postgres
db_shell:
	docker exec -it twitbooks_db psql -U test
build:
	docker build -t io.paulocosta/twitbooks-api .
run:
	docker run -it --rm -p 8080:8080 io.paulocosta/twitbooks-api
