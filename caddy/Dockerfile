FROM caddy:latest
RUN apk update && apk add nss-tools
ADD Caddyfile /etc/caddy/
#VOLUME ["/etc/letsencrypt/"]
CMD ["caddy", "run", "--config", "/etc/caddy/Caddyfile", "--adapter", "caddyfile"]

