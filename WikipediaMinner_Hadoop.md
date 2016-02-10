

# Useful Links #
  * Pricing: http://aws.amazon.com/elasticmapreduce/pricing/
  * The Amazon Elastic MapReduce Developer Guide (pdf) is a very good resource for most issues, including default options for maximum tasks per node and default options: http://www.technicalwritingprofessional.com/images/emr-dg.pdf

# Stage -1: Splitting Wikipedia Datadump #
Use feedSplit.sh script to split the Wikipedia dump into many pieces to be uploaded to S3.  feedSplit takes 4 arguments: the dump file, the split file (a wikiMiner script in Python), the prefix for output files, and the number of splits to make.  The number of splits has been 100 in the past.


# Stage 0: DocIDGenerator #
It generates ID and names for the chosen Wikipedia pages.
## Optimizations ##
  * Excludes any disambiguation, empty, redirect and stub pages
  * Excludes any words in the set of stop words
## Initial Input ##
  * Wikipedia pages
## Final output ##
  * A page name and ID (drama in the 1970's "\t" 12341367) a copy of this output is in MongoDB in the Poliwiki server
## Mapper ##
  * Key - this is not the Wikipedia page ID, it is only a long value showing where in the file the page is
  * Value - this is the actual Wikipedia page: contains the actual text, page ID and other information.
## Reducer ##
  * Key - page Title
  * Value - page ID
## Hadoop job logistics ##
  * Can use the medium Amazon elastic cluster to run it ( 20 mappers and 20 reducers)
  * The input file is "s3://macademia/wiki/input" and the output file is "s3://macademia/step0"
## Notes ##
  * Used 10 large machines, with 50 mappers and 20 reducers, on data dump from April 2011. Took < 20 minutes.


# Stage 0.5: Produce rankedpages.txt #
First, download the output from the previous step and concatenate it. Then use wikiRankWrap.sh to produce list of ranked pages to conduct analysis on. Arguments are date (in format yyyymmdd, the wikiRanker.py file, the concatenated output, and the name of the output file (a text file). Upload this to Amazon.  Using 20 hours took about 10 minutes.

# Stage 1: Wordcounter #
It takes each Wikipedia page and counts how many times a word appears in the page.
## Optimizations ##
  * Only add pages that are in the ranked\_pages file. The file is produces by a python file that is produced by combining the wikipedia\_cache and pagecounts files. This allows to only add pages past a certain rank, no hard rank threshold needed.
## Initial Input ##
  * wikipedia pages and rankedpages.txt
## Final Ouput ##
  * A word, the wikipedia page ID, the number of times the word appears in the page and total number of words processed in that page (developed   @345263746@5@6249).
  * A word and the total count of Wikipedia pages from which the word was processed (developed#c1234).
## Mapper ##
  * Key - this is not the Wikipedia page ID, it is only a long value showing where in the file the page is
  * Value: Wikipedia page
## Reducer ##
  * Key - word
  * Value: a Wikipedia page, number of times the word appeared in the page, the number of words in the page.
## Hadoop job logistics ##
  * Can use the small Amazon elastic cluster to run it ( 20 mappers and 20 reducers)
  * Takes three arguments: the first is the input used in stage 0, the second is the output file location, and the third (an optional argument) is the ranking produced in stage 0.5
  * The arguments might look something like: "s3://macademia/wiki/input s3://macademia/step1 s3://macademia/wiki/rankedpages.txt"
## Notes ##
  * Using 10 large machines with 50 mappers and 20 reducers took a little over 30 minutes

# Stage 2:Docfrequency #
Prepares the data pieces for the tf-idf calculations

## Initial Input ##
  * output from step 1.
## Final Ouput ##
  * Each line contains a Wikipedia page ID, a word, the number of times the word appears in the page, the length of the page and the number of pages in which the word appears (1234567   developed@345@6249@32424).
## Mapper ##
  * Key - word
  * Value1 - A word, the wikipedia page ID, the number of times the word appears in the page and total number of words processed in that page (developed   @345263746@5@6249).
  * Value2 - A word and the total count of Wikipedia pages from which the word was processed (developed#c1234).
## Reducer ##
  * Key - word
  * value -   Each line contains a Wikipedia page ID, a word, the number of times the word appears in the page, the length of the page and the number of pages in which the word appears (1234567   developed@345@6249@32424).
## Hadoop job logistics ##
  * Can use the medium Amazon elastic cluster to run it ( 20 mappers and 20 reducers)
  * The input file is "s3://macademia/step1" and the output file is "s3://macademia/step2"
## Notes ##
  * Using 10 large machines with 50 mappers and 20 reducers took about 11 minutes

# Stage 3:Tfidfvectorpruner #
This is used to calculate the tf-idf scores

## Optimizations ##
  * Set the maximum number of pages in which a word appears to be a sixth of the total number of pages in the whole of Wikipedia and the minimum to be 3.
  * Set the vector size to 600. Used the python script wiki\_stats.py to bin pages according to their number of words. We found that capping the # of words to 600 will cover all the words for 90% of documents, and that the remaining words are unlikely to be interesting.
  * Set smoothing constant to 100 to penalize pages in which the words have less weight, for more ask Shilad.
## Initial Input ##
  * output from stage 2
  * 1800000 (1.8 million) which is the total number of wikipedia pages by February 2010.

## Final Output ##
  * Each line is an article, a word and the tf-idf score (34567821  developed@0.08).
## Mapper ##
  * Key - article
  * Value - a word, the number of times the word appeared in the page that is the key, the number that word appeared in other pages and the length of the page.
## Reducer ##
  * Key - article
  * Value - word and tf-idf score
## Hadoop job logistics ##
  * Can use the large Amazon elastic cluster to run it ( 50 mappers and 50 reducers)
  * The input file is "s3://macademia/step2" and the output file is "s3://macademia/step3" and the number of pages additional input is 1800000.
## Notes ##
  * Took about 11 minutes using 10 large machines and default options


# Stage 4: Docsimscorer #
Calculates the similarities between two documents

## Optimizations ##
  * Set the maximum number of documents per terms to 1500 such that we have (1500)^2 article pairs for each word. 2000 will make the output files too big.
  * Set the number of splits to 1 and split index to 0, because we can do all the data at once, because Amazon has the resources we need.
## Initial Input ##
  * Output from step 3
## Final Output ##
  * Each line has an article pair and their combined scores.
## Mapper ##
  * Key - article1
  * Value - article2@score
## Reducer ##
  * Key - article1@article2
  * Value - combined score (score1\*score2)
## Hadoop job logistics ##
  * Can use the large Amazon elastic cluster to run it ( 100 mappers and 200 reducers)
  * The input file is "s3://macademia/step3" and the output file is "s3://macademia/step4" and additional inputs are 1 and 0 for number of splits and split index.
## Notes ##
  * Attempted with 160 mappers and 160 reducers using 20 extra large machines and failed due to some output file sizes being larger than 5GB, which is the largest size S3 will accept in one put. CPU was the limiting factor, expected running time was around 4 hours.
  * Took 2 hours, 51 minutes using 20 extra-large high CPU machines with 160 mappers and 320 reducers.  Limiting factor appeared to be the number of reduce jobs allowed, which was 4.  The command line argument "-D mapred.tasktracker.reduce.tasks.maximum=8" should set this value to 8.

# Stage 5: Finaldocsim #
This adds the similarity scores for the pair of wiki articles being compared

## Initial Input ##
  * output from step 4
## Final Output ##
  * article pairs and the sum of all of their partial scores across all of the words
## Mapper ##
  * Key - article1@article2
  * Value - score
## Reducer ##
  * Key - article1@article2
  * Value - sum of score across pairs of articles
## Hadoop job logistics ##
  * Can use the large Amazon elastic cluster to run it ( 100 mappers and 200 reducers)
  * The input file is "s3://macademia/step4" and the output file is "s3://macademia/step5"
## Notes ##
  * Test run using two large machines and 320 mappers revealed that a map job took about 90 minutes. Limiting factors were CPU while receiving the file and then disk writes while writing the intermediate file.  We considered compressing the intermediate files, but decided not to explore that option further for the time being.
  * Attempted to run with 20 large machines, ran out of memory. Used default map options.
  * Attempted to run with 20 large, high-memory machines and failed a second time due to output files being too large with 100 reduce tasks.
  * Ran with 20 large, high-memory machines and succeeded in about 13 and a half hours.

# Stage 6: JSondocsim #
returns output that is formatted to be inserted into MongoDB

## Initial Input ##
  * output from step 5
## Final Output ##
  * article1 and all the articles that are related to it sorted by scores (article 1 article 2, score2 | ... | article n|score n)
## Mapper ##
  * Key - article1@article2
  * Value - score
## Reducer ##
  * Key - article1
  * Value - \"article2,score2 | article3,score3 ... \"
## Hadoop job logistics ##
  * Can use the large Amazon elastic cluster to run it ( 100 mappers and 100 reducers)
  * The input file is "s3://macademia/step5" and the output file is "s3://macademia/step6"
## Notes ##
  * Tried using 20 large machines and failed due to lack of memory.  There may have been other reasons as well.

# Stage 7: Load to MongoDB #
Move the files to the server and use removeQuotes.py to remove unnecessary quotes from the beginning and end of the keys and values if they exist.  Finally, use the load.sh script to load the output into MongoDB.

Useful command for pulling down the files from S3:
```
seq 0 400 | xargs printf "%05d\\n" | \
parallel -P 10 --progress --eta s3cmd --no-progress sync s3://macademia/nbrvz/joined/part-r-{}.gz .
```