# Accounts #
To be a member of the Poliwiki team, your email must be added to:
  * The Hudson integrated build server
  * Three sites:
    * This site (the Macademia google code site) for the development team
  * Two google groups:
    * [Poliwiki Dev](http://groups.google.com/group/poliwiki-dev) - developer discussions between team members.
    * [Poliwiki Svn](http://groups.google.com/group/poliwiki-svn) - subversion commit messages.

# Integrated Build #
We have set up a continuous integrated build server at http://poliwiki.macalester.edu:8080/hudson/.  The server checks out the codebase from svn, builds the codebase, and runs the unit tests.  If things go wrong, you will receive an email.

# IP Address/MAC Address #

To find your IP Address: Go to system preferences > network > ethernet

To find your MAC Address: From the aforementioned step, click “advanced”, then ethernet. the MAC Address should be “Ethernet ID”

# Software Installation #
### Install grails ###

For PCs see: http://www.grails.org/Installation

For Macs:
  1. Download the binary zip of grails 1.3.7 and open it up.  It should be in Downloads/grails-1.3.7.
  1. Add the following three lines to the ".profile" file in your home directory.  You may have to create the file if doesn't exist.  You can use any editor you would like, but make sure it is in "plain text" mode.  TextEdit ships with the Mac.
```
export GRAILS_HOME=~/Downloads/grails-1.3.7
export PATH=$PATH:$GRAILS_HOME/bin
export JAVA_HOME=/Library/Java/Home
```
You can check to make sure this worked properly by opening a terminal and typing echo $GRAILS\_HOME.  You should see the information you entered.

_Note: In order to show your .profile file if it exists and it is hidden, type the following in terminal:_
```
defaults write com.apple.finder AppleShowAllFiles TRUE
killall Finder
```
_To Hide again, just change TRUE to FALSE_

### Setup Intellij ###
  1. Download (http://download.jetbrains.com/idea/ideaIU-12.1.3.dmg) and install Intellij.  You want the ultimate edition.
  1. Start up IntelliJ.  You'll need to answer some configuration questions:
    1. For now we'll use the evaluation license (or ask Shilad for the license).
    1. You need to select the subversion and Git VCS plugin.
    1. Select all the Web / J2EE plugins.
    1. Only select the tomcat and Resin app server plugins.
    1. Select all the HTML / Javascript plugins.
    1. Select all the "Other" plugins.
    1. The first time you create a Java or grails project you'll need to specify a path to a Java Compiler.  Choose "a new JCS," and use the path you specified for JAVA\_HOME in your .profile.

### Check out Macademia ###
  1. Check out Macademia from version control.
    1. Open intellij and click "check out from version control."
    1. Choose Subversion
    1. The repositories tab will open.  Click the "+" to add the following repository: https://macademia.googlecode.com/svn/trunk
    1. When the repository opens, it will show the address https://macademia.googlecode.com/svn/trunk
    1. Click on the arrow to open the file, and you will see the "Macademia" folder; click on that and click checkout
    1. You should checkout the project into a folder that ends with "/Macademia" (probably the second of three options).
      * If you are using a computer in the Maclab, your flash drive will be under 'Volumes' at the very bottom of the list IntelliJ gives you.
  1. Configure the project
    1. After checking out will ask you if you want to create a grails application for the checked out files, click yes
    1. Then click on import grails application from existing source
    1. It will ask for a JDK, click the "+" and select your JAVA\_HOME directory: /Library/Java/Home
    1. Then it will ask you to choose the Grails SDK, choose the grails folder in Users/yourname/Downloads/grails-1.3.7
    1. Click to continue an create the project
  1. Initialize the grails project:
    1. run grails upgrade: tools > run grails target, type "upgrade" as the Command line.
    1. You will have to click on the scrolling text in the console and type "y" to continue.

### Installing MacPorts (Mac only) ###
  1. Download the appropriate dmg from the Macports installation page: http://www.macports.org/install.php
  1. restart your terminal, if it is already open
  1. sudo port -v selfupdate
  1. sudo port upgrade outdated
  1. If you get an error on the previous step, try: "sudo port -f activate python24 @2.4.6\_9" and then try "sudo port upgrade outdated" again

### Install MongoDB: ###
  1. sudo port install mongodb

### Install Postgres: ###
  1. sudo port install postgresql90
  1. sudo port install postgresql90-server
  1. sudo mkdir -p /opt/local/var/db/postgresql90/defaultdb
  1. sudo chown postgres:postgres /opt/local/var/db/postgresql90/defaultdb
  1. sudo su postgres -c '/opt/local/lib/postgresql90/bin/initdb -D /opt/local/var/db/postgresql90/defaultdb'
  1. sudo port load postgresql90-server

### Configure Postgres ###
Create user “grails” with password “grails”: sudo -u postgres /opt/local/lib/postgresql90/bin/createuser -P grails -s

**Create databases:**

  1. /opt/local/lib/postgresql90/bin/createdb -Ugrails macademia\_test\_research
  1. /opt/local/lib/postgresql90/bin/createdb -Ugrails macademia\_dev\_research
  1. /opt/local/lib/postgresql90/bin/createdb -Ugrails macademia\_prod\_research

### Configure and start  MongoDb ###

  1. In a new terminal:
    1. sudo mkdir -p /data/db
    1. sudo chown -R research /data/
    1. mongod
  1. Always keep this running while you develop.

### Load production data into the system: ###
(from the directory you checked Macademia out to)
  1. ./scripts/restoreDb.sh

### Running Macademia ###
  1. Before running macademia, there are some things that must be done.
    1. in your terminal, navigate to your project and type: svn update
    1. copy MacademiaConfig.example.groovy (in grails-app/test), paste a copy in to the same location and rename it MacademiaConfig.groovy.
    1. in that file, create your own uniqueDbToken value.
    1. finally, type: grails PopulateDev && grails PopulateTest
  1. now type "grails run-app" in your terminal (make sure you've navigated to your directory) and it should be done

## Intellij Issues and Subversion ##
  1. When you start up, sometimes intellij will not use subversion as your default form of version control. To enable this, go to "version control" (at the top), and then click "enable version control integration". A pop up window will appear, with a scroll-down bar. Select "subversion" from the scroll down bar. You're set. Now you can right-click and go to subversion > update directory... to update your project.
  1. Also, if Intellij starts to not use your java SDK (ie, all your classes don't work, even the word "String" is unlined with red"), simply go to File > project structure and change the project SDK (probably select java sdk, then "current sdk" from the drop down folder)

# Code Reviews #
The code review site is available at http://codereview.macalester.edu/.  Information and help available at http://code.google.com/p/rietveld/wiki/CodeReviewHelp

### Instructions ###
  * Download the [upload.py](http://codereview.macalester.edu/static/upload.py) script (it requires python-2.5)
  * Prepare some changes to check in to subversion
  * Make sure to "svn add" your files before executing the code review.  If you don't svn add a file before running upload.py, that file won't be included in the code review.  You can add the file (or directory) foo from the command line by running "svn add ./foo" from the containing directory.  If you forget to add the file, upload.py will warn you (we've just been ignoring the warnings :).
  * Initiate a code review by running the following from your Macademia directory:
```
scripts/code-review.sh ./foo ./bar
```
  * The arguments should be the files or directories you have changed.
  * You will be prompted for your macalester email address and password and a description of the change.
  * Shilad will review your change, and you'll receive an email when it's ready.
  * Commit your change **after** the code review is complete.

### Setting up IDE Talk ###
IntelliJ's IDE talk feature allows you to chat with other macademia developers and it allows chattees to easily view each other's working source code files.  To use your gmail account:
  * Select "use an existing account"
  * Username is your full gmail address.
  * Password is your full gmail password.
  * server is talk.google.com

### Other notes ###
Sometimes code reviews will become confused about your password, or you'll want to change your login information.  You can make upload.py forget about your macalester account by removing the .codereview\_upload\_cookies file in your home directory.