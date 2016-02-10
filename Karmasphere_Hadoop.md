## Wikipedia Hadoop ##
Karmasphere is a Hadoop plugin for Netbeans. It allows programmers to
interact with the Hadoop filesystem and run jobs. We are using it to
calculate the similarity of our wikipedia pages.

## Installing Karmasphere ##
  * Install Netbeans (http://netbeans.org/downloads/index.html -choose the java version)
  * In Netbeans go to plugins  and clique settings
  * On settings click the button add and enter the following information
  * Name: "Karmasphere for Hadoop" and URL: "http://hadoopstudio.org/updates/updates.xml"
  * Click ok and go to available plugins tab and search for karmasphere
  * About four items will appear on the results, click all of them and press install

## Checking out the project ##
  * On Netbeans click on team and choose svn
  * Choose checkout and the URL is "http://poliwiki.macalester.edu/svn/research"
  * Enter your username and password and checkout the wikiMiner project, under macademia
  * Create a Java Project with Existing Sources
  * When asked to add source folders, choose the existing folder containing the wikiMiner project
  * Continue to the finish

## Setting up the Amazon Account ##
  * On Netbeans click on the services tab
  * Under Hadoop, right click on Amazon Accounts and choose New Amazon Account
  * Fill out the remaining information as directed by Shilad

## Keypairs ##
  * Keypairs can be created by using Amazon's Control Panel under EC2/Key Pairs

## Setting up the HDFS service in Netbeans ##
  * On Netbeans click on the services tab
  * Under Hadoop, right click on Remote Clusters and choose New HDFS Filesystem
  * Enter the Filesystem Name "Wikipedia\_Hadoop"
  * Click on HDFS and click next
  * Enter the NameNode host "mist.public.stolaf.edu"; port: 9000, your username and group
  * Press next and enter proxy type: "SSH TUNNEL", enter the SSH private key (see next section)
  * Press Finish
  * The new filesystem will appear under Hadoop Filesystems
  * Right click on it and press browse to see files

## Generating and setting the private key ##
  * In the terminal window enter ssh-keygen -t dss
  * On mist cd to .shh and create an authorization file (vim authorized\_keys)
  * On your machine (cat ./ssh/id\_dsa.pub)
  * Copy the key and go to mist and paste it into the authorized\_keys file
  * Test to see if the login to mist works with username and password
  * If it does that means that the key worked.
  * Then go back to the Netbeans IDE and browse to the ./ssh directory to upload the "id\_dsa" file into the private key.

## Setting up the Hadoop Cluster on Karmasphere ##
  * On the services tab click on Hadoop Clusters
  * Click on New Cluster
  * Enter the cluster name "mistRider"
  * In the "Cluster Type" enter "Hadoop cluster"
  * The default filesystem will point to the filesystem you have created
  * You have to enter the host name (mist.public.stolaf.edu) and port (9001)
  * Press next then enter the proxy type as SSHTUNNEL: and your username and the private key should be autocompleted, if not follow the smae steps as when setting up the filesystem in order to obtain the private key.
  * Press finish

## Setting up an Amazon Cluster ##
  * Expand a created Amazon Account in the Services tab, right click on JobFlow Templates, and choose the Create New option.
  * Name your new cluster.
  * On the next page, choose the S3 bucket you are using and choose the EC2 SSH key name, which Karmasphere will use to allow you to track any jobs you run on this cluster.
  * You may also choose the number and type of computers you want to use here.  The instance type is followed by the number of EC2 compute units, amount of memory, and amount of local storage).  Due to the presence of relatively large revisions, jobs can be memory intensive at times and we have had the most success running jobs with memory equal to at least that of the high-memory extra-large (~17GB).
  * Click finish.

## Setting up  the Hadoop job ##
  * In the Services tab, right click on Hadoop Jobs and choose New Hadoop Job (from pre-existing jar file)
  * Name your job
  * The primary jar file is wikiminer-map-reduce/dist/wikipedia-map-reduce.jar.  This file is rebuilt every time you use the build and clean option.
  * Choose the main class for the job from the list provided in the Select menu at the right.
  * Choose the cluster you want to use from the drop down menu and enter the default arguments below that.  The last two arguments are the input and output directories, respectively.  Be careful here, because in order for the job to succeed, the output directory must initially be empty.  In order to ensure this, many of the jobs in our library delete the output directory as part of their initialization.  NOTE: the Embedded Hadoop clusters will run a version of hadoop on the local machine.  This can be useful for bug fixing, though do remember that output directories will still be deleted beforehand.
  * At this point making further changes to the form is unnecessary and you may click Finish.