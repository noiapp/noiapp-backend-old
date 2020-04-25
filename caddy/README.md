# Caddy https proxy

## This Caddyfile is for dev environments, until you don't have a real DNS domain to be checked during auto-certificate generation, you should:
* use "tls internal" in Caddyfile:
* use curl with -k (--insecure option) to tell curl to trust demo.protetti.app's certificate

## Example:
```
$ docker-compose -f app.yml up

$ curl -k https://locahost/v1
Hello from DP3T WS

$ curl http://localhost/v1
Hello from DP3T WS
```

