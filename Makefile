heroku-create:
	heroku create ner-service --remote ner
	heroku create twitbooks-api-service --remote api
	heroku buildpacks:add -a ner-service https://github.com/heroku/heroku-buildpack-multi-procfile
	heroku buildpacks:add -a twitbooks-api-service https://github.com/heroku/heroku-buildpack-multi-procfile
	heroku config:set -a ner-service PROCFILE=ner_service/Procfile
heroku-deploy-ner:
	git push https://git.heroku.com/ner-service.git HEAD:master
