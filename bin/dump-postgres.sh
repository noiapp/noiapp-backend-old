#!/bin/bash

DATE=$(date +"%Y-%m-%d-%H-%M")
DUMPDIR=/home/noiapp/dump
DESTFILE=$DUMPDIR/dump-$DATE.tar
LATEST=$DUMPDIR/latest

mkdir -p "$DUMPDIR"

# exec dump
docker exec noiapp_noiapp-database_1 bash -c 'pg_dump --create --format t -U noiapp'>"$DESTFILE"
RET=$?
if [ $RET -ne 0 ] ; then
  rm "$DESTFILE"
  echo "DB dump has failed, $DESTFILE not created." >&2
  exit $RET
fi

# create latest link
rm $LATEST
ln -s "$DESTFILE" "$LATEST"

# retain only last 2 dumps and latest link
find $DUMPDIR -type f -printf '%T@\t%p\n' | sort -t $'\t' -g | head -n -2 | cut -d $'\t' -f 2- | xargs rm
