# Problems checking out Macademia #

We've found that occasionally when Macademia is checked out from the svn it doesn't work initially. Here are solutions to problems we've encountered.

## _File not found_ error ##

If you run the app and it comes up with a _file not found_ error, it could be that the upgrade (detailed in the [Installation](http://code.google.com/p/macademia/wiki/Installation) guide) didn't work. To remedy this, open up the terminal and (in your Macademia file) type 'grails clean', then 'grails upgrade'. That should fix the problem.

## jQuery issues ##

If the app runs and opens the page only to tell you that jQuery is not defined, simply uninstall the jQuery plugin and reinstall it. Do this from the terminal with the commands 'grails uninstall-plugin jquery' then 'grails install-plugin jquery'. This problem may arise as a result of using [nimble](http://code.google.com/p/macademia/wiki/Nimble), but we don't know for sure.

## Reinitializing the database ##
There are times when rebuilding/repopulating the service is required (especially when you use/add new variables to the classes.
First, find the database folder in your project folder (use the ls command). Usually:
/Users/name/Macademia/db/dev/full

Navigate to your project folder and use the following command:
rm ./db/dev/full/
you should see many previous versions. An example:

.svn/                  devDb.properties.[r96](https://code.google.com/p/macademia/source/detail?r=96)   devDb.script.1.[r96](https://code.google.com/p/macademia/source/detail?r=96)
devDb.properties       devDb.script           devDb.script.mine
devDb.properties.mine  devDb.script.1.mine    devDb.script.[r113](https://code.google.com/p/macademia/source/detail?r=113)
devDb.properties.[r113](https://code.google.com/p/macademia/source/detail?r=113)  devDb.script.1.[r113](https://code.google.com/p/macademia/source/detail?r=113)    devDb.script.[r96](https://code.google.com/p/macademia/source/detail?r=96)

Then run the following command to delete the existing database:
rm ./db/dev/full/devDb.(asterisk)

Following that, rebuild the database with the following command:
grails populate

When that is done (it will take a while), run the following command:
grails buildInterestRelations

Now try again :)

# Problems with IntelliJ #

## IntelliJ runs out of memory ##
  * If IntelliJ itself runs out of memory, [increase the memory for the IDE](http://stackoverflow.com/questions/2435569/how-to-give-more-memory-to-intellij-idea-9)
  * If programs that IntelliJ starts (such as grails) run out of memory:
    * Click "edit configuration" from the drop down menu of runnable items at the top-left of the IDE (right next to the green arrow).
    * Click on the runnable item whose memory you want to increase.
    * Add the following VM parameter: -Xmx1024M (or any other reasonable value).