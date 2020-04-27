#!/bin/bash
BACKUPHOST=protetti2
rsync -essh -avv --delete ~/dump/ noiapp@${BACKUPHOST}:~/dump/
rsync -essh -avv --delete ~/app.yml noiapp@${BACKUPHOST}:~/app.yml
rsync -essh -avv --delete ~/bin/ noiapp@${BACKUPHOST}:~/bin/

