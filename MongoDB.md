A Description of how MongoDB is used and how to interact with it.

# MongoDB #

MongoDB has many databases and each of these databases have many collections which store the records or actual data.

The dev database currently stores the information for the dev build which includes all of the Macalester information that we have. The test  database holds info for 5 people from Macalester. The benchmark database holds a synthetic dataset with 1000 people. The database fromWikipedia holds information on wikipedia articles and their similarities and **SHOULD NOT BE DROPPED**.

In the dev, test, and benchmark databases there should be 5 collections **users, interests, collaboratorRequests, articlesToInterests, institutionInterests**.

The **users** collections holds information on the people. The record has fields for _id, institution, interests. All of this fields are populated with ids that correspond to PostgreSQL or GORM. The interests field should hold a list of interest ids and should be **indexed**._

The **interests** collection holds information on related interests. The record has fields for  _id, similar. The_id field is the id of the primary interest and the similar field is a string of similar interests of the form interestId,similarityScore| concatenated together. There are method to easily convert these entries into SimilarInterestList.

The **collaboratorRequests** collection hold the information about collaborator requests. The record has fields for _id, creator, institution, keywords. All the fields consist of the corresponding ids. The keywords field is very similar it the interests field in the **users** collection and also  should be **indexed**._

The **articlesToInterests**  collection holds a mapping of article ids to interests that are associated with that article. The record has two fields _id which is the article id and interests which is a string containing the interest ids separated by commas._

The **institutionInterests** collections hold the information of which interests are at which institution. The record has fields for _id which is the institution id and interests which are interest ids separated by commas._

The database fromWikipedia holds information about articles and their pairwise similarities it has two collections **articlesToIds**  and **articleSimilarities**.

The **articlesToIds** collection maps article titles to their ids. It has two fields _id which is the name of the articles and wpId which is the article's id  number._

The **articleSimilarities** collection maps each wikipedia article to its most similar wikipedia articles. It has two fields _id which is the article id and similar which is a string that has the similar articles id and similarity score in a form similar to the similar field in the **interests** collection_



# Indexing #

Indexing can be done through the mongodb shell which is available along with installation instructions http://www.mongodb.org/downloads and for additional reference on using the shell look here http://www.mongodb.org/display/DOCS/mongo+-+The+Interactive+Shell. To add an index using the shell first enter the command "use" followed by the name of the database that you are adding the index to. Then enter "db.collection.ensureIndex({field:1})" where field is the name of the field that you wish to index and collections is the name of the collection that hold the appropriated records. Also note that the field _id is automatically indexed_

## Currently Required Indexes ##

db.articlesToIds.ensureIndex({'wpId' : 1}) for both wikipediaReadOnly and wikipediaReadOnlyTest

db.users.ensureIndex({'interests' : 1}) for macademia\_prod