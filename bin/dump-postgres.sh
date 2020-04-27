#!/bin/bash

DATE=$(date +"%Y-%m-%d-%H-%M")

DUMPDIR=/home/noiapp/dump
DESTFILE=$DUMPDIR/dump-$DATE.tar
LATEST=$DUMPDIR/latest
ls -t | tail -n +5 | xargs rm --
docker exec noiapp_noiapp-database_1 bash -c 'pg_dump --create --format t -U noiapp'>$DESTFILE
rm $LATEST
ln -s $DESTFILE $LATEST
ls -t $DUMPDIR | tail -n +4 | xargs rm --
