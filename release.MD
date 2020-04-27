#Release

1. Create docker image for backend: `make docker-build`
1. Push docker image on docker hub: `docker push noiapp/noiapp-backend:develop`
1. Log on server1
1. Stop all: `docker-compose -f app.yml stop`
1. Get new images: `docker-compose -f app.yml pull`
1. Start all: `docker-compose -f app.yml up -d`
1. Repeat on server2

# Disaster recovery
Use script under bin to sync a DR server using crontab:

Main server
```aidl
# backup noiapp db
5 * * * * noiapp /home/noiapp/bin/dump-postgres.sh 2>&1
# rsync db dump and application files
7 * * * * noiapp /home/noiapp/bin/rsync.sh 2>&1
```
DR Server
```aidl
# restore noiapp db
9 * * * * noiapp /home/noiapp/bin/restore-postgres.sh 2>&1
```

##dump-postgres.sh
1. Create a tar dump of postgres
1. Retain only last 2 dumps

##restore-postgres.sh
1. Import latest dump on db deleting an recreating all 

##rsync.sh
1. Copy db dumps
1. Copy bin directory
1. Copy app.yml
