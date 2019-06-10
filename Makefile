heroku-create:
	# Creating Apps
	heroku create ner-service --remote ner
	heroku create twitbooks-api-service --remote api # NER service config heroku buildpacks:add -a ner-service https://github.com/heroku/heroku-buildpack-multi-procfile
	heroku buildpacks:add -a ner-service https://github.com/heroku/heroku-buildpack-python
	heroku config:set -a ner-service PROCFILE=ner_service/Procfile
	heroku config:set -a ner-service WEB_CONCURRENCY=1
	# API service config
	heroku buildpacks:add -a twitbooks-api-service https://github.com/heroku/heroku-buildpack-multi-procfile
heroku-deploy-ner:
	git push https://git.heroku.com/ner-service.git HEAD:master
heroku-delete:
	heroku apps:destroy ner-service
	heroku apps:destroy twitbooks-api-service
registry-login:
	docker login registry.gitlab.com -u ${GITLAB_USER} -p ${GITLAB_PASS}
push-ner: registry-login
	docker build -t registry.gitlab.com/paulombcosta/twitbooks/ner:latest ner_service/
	docker push registry.gitlab.com/paulombcosta/twitbooks/ner:latest
push-api: registry-login
	docker run -it --rm -v ~/.m2:/root/.m2 -v $(shell pwd)/backend/target:$(shell pwd)/target -v /var/run/docker.sock:/var/run/docker.sock -v $(shell pwd)/backend:$(shell pwd) -w $(shell pwd) maven:3-jdk-8-alpine mvn package -DskipTests
	docker build -t registry.gitlab.com/paulombcosta/twitbooks/api:latest backend/
	docker push registry.gitlab.com/paulombcosta/twitbooks/api:latest
create-secrets:
	kubectl create secret docker-registry gitlab-registry --docker-server=registry.gitlab.com --docker-username=${GITLAB_USER} --docker-password=${GITLAB_PASS} --docker-email=${GITLAB_USER}
	kubectl create secret generic api-env \
		--from-literal=DB_URL=${DB_URL} \
		--from-literal=DB_USER=${DB_USER} \
		--from-literal=DB_PASS=${DB_PASS} \
		--from-literal=GOODREADS_KEY=${GOODREADS_KEY} \
		--from-literal=TWITTER_CONSUMER_KEY=${TWITTER_CONSUMER_KEY} \
		--from-literal=TWITTER_CONSUMER_SECRET=${TWITTER_CONSUMER_SECRET} \
		--from-literal=TWITTER_ACCESS_TOKEN=${TWITTER_ACCESS_TOKEN} \
		--from-literal=TWITTER_ACCESS_TOKEN_SECRET=${TWITTER_ACCESS_TOKEN_SECRET}
build-front:
	cd frontend && npm install && npm run build
	cp frontend/_redirects frontend/build/_redirects
helm-setup:
	kubectl create serviceaccount --namespace kube-system tiller
	kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
	helm init --service-account tiller
ngnix-setup:
	# Install ngnix ingress on master node
	helm install stable/nginx-ingress --namespace kube-system
	# Create ingress rule to reverse-proxy requests to the api pod
	kubectl apply -f deployment/ingress.yml
tls-setup:
	helm install stable/kube-lego --namespace kube-system --set config.LEGO_EMAIL=${EMAIL},config.LEGO_URL=https://acme-v01.api.letsencrypt.org/directory,rbac.create=true
heapster-setup:
	helm install stable/heapster --namespace kube-system
