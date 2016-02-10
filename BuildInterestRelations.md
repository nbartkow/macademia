An overview of the algorithm for building interest relations and the relevant database tables.

## Introduction ##

Building interest relations is necessary for building the graph displayed on Macademia.  The method is used when a user adds a new interest that does not yet exist in the database.

## The Database Tables ##

In order to build interest relations three separate MongoDB database collections are used to update a fourth collection.  All the values in the collections are in the form of strings, in order to optimize running time.

The first collection, **articlesToIds**, is used to map from an article name to an article ID.  The article IDs are taken from the Wikipedia articles they represent.  Throughout the rest of the database, article refers to an article ID.

The second collection, **articlesToInterests**, maps from an article ID to interest IDs.  The interest IDs are the same as the interest IDs in Gorm.

The third collection, **articleSimilarities**, maps from an article ID to the IDs of the 20k most similar articles and their similarity scores.

The collection that is updated, **interests**, maps from an interest ID to the IDs of the 200 most similar interests, sorted in descending order.

## The Algorithm ##

Whenever a user inputs a new interest into the database it must be inserted into the **interests** collection.  In order to do so, the algorithm first uses GoogleService to find the Wikipedia article that interest should be associated with.  Using the **articlesToIds** collection this information is translated into the Wikipedia article ID.  Then the algorithm adds the new interest to the string of interests associated with that article in the **articlesToInterests** collection.

At this point, the algorithm begins to look for interests that are very similar to the new interest by checking for other interests that share the same article.  These are added to the **interests** collection under the new interest's entry with a similarity score of 1.

Then the **articleSimilarities** collection is used to find other articles that are similar to the original article.  For each article, the algorithm checks for interests associated with that article in **articlesToInterests**.  These interests are also added to the **interests** collection under the new interest's entry, but with a similarity score that is equal to the similarity score between the two articles.  When the new interest has  200 similar interests, the algorithm stops looking for new interests.