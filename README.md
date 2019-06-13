# TwitBooks

## What it is ?

Twitbooks is an app that extracts book suggestions from your Twitter friends.
It consists of a Spring Boot project that synchronizes Twitter data and provides
an API endpoint for clients. It integrates with another service that uses spaCy's
[Named Entity Recognition](https://spacy.io/usage/linguistic-features#named-entities)
features to identify works of arts and that information is fed to [Goodreads](`https://www.goodreads.com/)
API to fetch book data.

## Running

You need to provide both Twitter and Goodreads API keys to the `backend` project. They can be set
through the respective environment variables:
```
```

## Cluster setup

This documentation to myself so I can refer to if the need arises that I have to provision a new
cluster again. After the cluster is created the first step is to install helm/tiller. This is
done by running the command:

```
make helm-setup
```

Use `kubens` to switch to the kube-system namespace and check that the tiller pod is running.

Our application uses secrets to obtain information like database url and etc. For it to have
access to them you have to create a secret resource containing that information. For this project
I just take these data from my machine's environment and put them into the secret. To do that run
the command:
```
make create-secrets
```

Now create your services and deployments:
```
kubectl apply -f deployment/api-service.yml
kubectl apply -f deployment/api-deployment.yml
kubectl apply -f deployment/ner-service.yml
kubectl apply -f deployment/ner-service.yml
```

Before we boot up the `nginx-ingress` he is already configured to use the letsencrypt certificates.
So let's create them first. Run the command.
```
helm tls-setup
```
This creates the cert-manager container that will request the certificates. Now you need to create
the issuer resource. You can do this by running:
```
kubectl create -f deployment/prod_issuer.yml
```

What's the difference between the prod and staging issuer ? The staging issuer is just to verify
that the setup is done properly and to not risk doing too many requests to letsencrypt and get
rate limited. To switch between prod and staging configs you have to change these two lines in
the `ingress.yml` file:

```
certmanager.k8s.io/cluster-issuer: twitbooks-<staging/prod>
secretName: twitbooks-<staging/prod>
```

Now all that's left is to create the `nginx-ingress` that will serve as our load balancer.
After the TLS setup is done properly as described earlier, just run the command:
```
make nginx-setup
```
Most problems happen during this stage. Mostly by the certificate not being generated. So
there are a few commands that help us troubleshoot that. First let's find the external
IP of our load balancer. Switch to the `kube-system` namespace and run
```
kubectl get services
```
You should see either an external IP already or a <pending>. In case of the latter just wait
for the IP to become available. If you try to access the IP directly it will fail because
NGINX is setup to use our DNS name. So you have to run a `curl` like this:
```
curl -kivL -H 'Host: api.twitbooks.io' 'http://<IP_GOES_HERE>/status'
```

If your service is running properly, this request should be successful.

To assert that the `nginx-ingress` has picked up the certificates properly. Run
```
kubectl describe ingress
```
Here it will show any error events that may have occurred or a succesful message if everything
has gone properly. In the case you need to restart the nginx ingress controller you can run
a patch on it like this: 
```
TODO patch command
```

Now you're done. All that is left is to point the DNS to the LoadBalancer IP and wait for it
to go live.

### A note on Heapster

The way my cluster is configured (at least on DO) there are no metrics on either nodes or pods.
If you run the command `kubectl top nodes` You get an error message saying that Heapster is
not installed. I've created the command to set it up and can be called via:
```
make heapster-setup
```

But on my case my cluster was already at its limit CPU-wise and the pods didn't get created. So
this part is untested for now.
