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
	docker run -it --rm -v ~/.m2:/root/.m2 -v $(shell pwd)/backend/target:$(shell pwd)/target -v /var/run/docker.sock:/var/run/docker.sock -v $(shell pwd)/backend:$(shell pwd) -w $(shell pwd) maven:3-jdk-10-slim mvn package -DskipTests
	docker build -t registry.gitlab.com/paulombcosta/twitbooks/api:latest backend/
	docker push registry.gitlab.com/paulombcosta/twitbooks/api:latest
create-registry-secret:
	kubectl create secret docker-registry gitlab-registry --docker-server=registry.gitlab.com --docker-username=${GITLAB_USER} --docker-password=${GITLAB_PASS} --docker-email=${GITLAB_USER}
create-credentials-secret:
	kubectl create secret generic api-env \
		--from-literal=DB_URL=${DB_URL} \
		--from-literal=DB_USER=${DB_USER} \
		--from-literal=DB_PASS=${DB_PASS} \
		--from-literal=GOODREADS_KEY=${GOODREADS_KEY} \
		--from-literal=TWITTER_CONSUMER_KEY=${TWITTER_CONSUMER_KEY} \
		--from-literal=TWITTER_CONSUMER_SECRET=${TWITTER_CONSUMER_SECRET} \
		--from-literal=AUTH0_APP_CLIENT_ID=${AUTH0_APP_CLIENT_ID} \
		--from-literal=AUTH0_AUDIENCE=${AUTH0_AUDIENCE} \
		--from-literal=AUTH0_API_CLIENT_ID=${AUTH0_API_CLIENT_ID} \
		--from-literal=AUTH0_API_CLIENT_SECRET=${AUTH0_API_CLIENT_SECRET}
delete-credentials-secret:
	kubectl delete secret api-env
create-secrets: create-registry-secret create-credentials-secret
build-front:
	cd frontend && npm install && npm run build
	cp frontend/_redirects frontend/build/_redirects
helm-setup:
	kubectl create serviceaccount --namespace kube-system tiller
	kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
	helm init --service-account tiller
nginx-setup:
	# Install ngnix ingress on master node
	helm install stable/nginx-ingress --namespace kube-system
	# Create ingress rule to reverse-proxy requests to the api pod
	kubectl apply -f deployment/ingress.yml
tls-setup:
	kubectl apply -f https://raw.githubusercontent.com/jetstack/cert-manager/release-0.8/deploy/manifests/00-crds.yaml
	kubectl label namespace kube-system certmanager.k8s.io/disable-validation="true"
	helm repo add jetstack https://charts.jetstack.io
	helm install --name cert-manager --namespace kube-system jetstack/cert-manager --version v0.8.0
tls-setup-old:
	helm install stable/kube-lego --namespace kube-system --set config.LEGO_EMAIL=${EMAIL},config.LEGO_URL=https://acme-v01.api.letsencrypt.org/directory,rbac.create=true
heapster-setup:
	helm upgrade --install --wait k8s-heapster stable/heapster --set=command='{/heapster,--source=kubernetes:https://kubernetes.default?kubeletHttps=true&kubeletPort=10250&insecure=true}' --namespace kube-system
