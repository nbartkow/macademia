#!/bin/sh

mongo wikipediaReadOnly --eval "db.articleSimilarities.drop()" &&
mongo wikipediaReadOnly --eval "db.articlesToIds.drop()" &&
gunzip < articlesToIds.txt.gz | mongoimport -d wikipediaReadOnly -c articlesToIds &&
gunzip <./articleSimilarities.txt.gz | mongoimport -d wikipediaReadOnly -c articleSimilarities
