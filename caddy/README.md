# Caddy https proxy

## This Caddyfile is for dev environments, until you don't have a real DNS domain to be checked during auto-certificate generation, you should:
* use "tls internal" in Caddyfile:
* add "127.0.0.1 demo.protetti.app" to your /etc/hosts file
* use curl with -k (--insecure option) to tell curl to trust demo.protetti.app's certificate

## Example:
```
$ curl -k https://demo.protetti.app/v1
Hello from DP3T WS
```

