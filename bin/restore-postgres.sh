#!/bin/bash
cat ~/dump/latest | docker exec -i noiapp_noiapp-database_1 pg_restore -c -U noiapp -d noiapp
