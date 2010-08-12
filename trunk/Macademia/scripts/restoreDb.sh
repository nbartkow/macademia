#!/bin/bash

export PGPASSWORD=grails
export PATH=/Library/PostgreSQL/8.4/bin/:/Users/shilad/Downloads/mongodb-osx-x86_64-1.4.4/bin:$PATH

URL_PREFIX=http://poliwiki.macalester.edu/shilad/data
MONGO_BACKUP=./db/prod.db.mongo
PSQL_BACKUP=./db/prod.db.psql

PSQL_DB=macademia_prod
MONGO_DB=macademia_prod

if ! [ -d $MONGO_BACKUP ]; then
    mkdir $MONGO_BACKUP
fi

wget $URL_PREFIX/`basename $MONGO_BACKUP`.tar.z -O - | tar -xpzf - && \
wget $URL_PREFIX/`basename $PSQL_BACKUP`.tar.z -O - | tar -xpzf - || 
    { echo "retrieval of dbs failed" >&2; exit 1; }

dropdb -U grails $PSQL_DB >&/dev/null

createdb -U grails $PSQL_DB && \
psql -U grails -d $PSQL_DB < $PSQL_BACKUP
mongorestore -d $MONGO_DB --drop $MONGO_BACKUP/$MONGO_DB
