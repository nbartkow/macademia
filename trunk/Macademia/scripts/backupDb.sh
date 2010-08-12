#!/bin/bash

export PGPASSWORD=grails
export PATH=/Library/PostgreSQL/8.4/bin/:/Users/shilad/Downloads/mongodb-osx-x86_64-1.4.4/bin:$PATH

MONGO_BACKUP=db/prod.db.mongo
PSQL_BACKUP=./db/prod.db.psql
WWW_DIR=/data/shilad/www/data

if ! [ -d $MONGO_BACKUP ]; then
    mkdir $MONGO_BACKUP
fi

pg_dump -U grails macademia_prod -f $PSQL_BACKUP && \
mongodump --db macademia_prod -o $MONGO_BACKUP && \
cp -p $PSQL_BACKUP $WWW_DIR/ && \
tar -cpz $MONGO_BACKUP >$WWW_DIR/`basename $MONGO_BACKUP` ||
    { echo "backup failed!">&2; exit 1; }
