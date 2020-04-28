#!/bin/bash

DATE=$(date +"%Y-%m-%d-%H-%M")
DUMPDIR=/home/noiapp/dump
DESTFILE=$DUMPDIR/dump-$DATE.tar
LATEST=$DUMPDIR/latest

mkdir -p "$DUMPDIR"

# exec dump
docker exec noiapp_noiapp-database_1 bash -c 'pg_dump --create --format t -U noiapp'>"$DESTFILE"

# create latest link
rm $LATEST
ln -s "$DESTFILE" "$LATEST"

# retain only last 2 dumps and latest link
ls -t "$DUMPDIR" | tail -n +4 | xargs rm --
