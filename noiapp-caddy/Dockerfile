FROM caddy:latest
ADD Caddyfile /etc/caddy/
VOLUME ["/etc/letsencrypt/"]
CMD ["caddy", "run", "--config", "/etc/caddy/Caddyfile", "--adapter", "caddyfile"]

