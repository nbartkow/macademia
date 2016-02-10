#How to upgrade Grails to 1.3.2

# Introduction #
This is how I did it and it seems to work. You may have to wrestle some bugs after, but I don't know for sure.

# Details #

  * Go to [Grails downloads](http://www.grails.org/Download) and download the binary zip of Grails 1.3.2.
  * If you remember how we set up grails initially (in the [installation instructions](http://code.google.com/p/macademia/wiki/Installation) section) we had to add three lines to the '.profile' file in our home directories. In order to find that again (because it was hidden) the only thing I could find was entering the following in the terminal:
> > defaults write com.apple.finder AppleShowAllFiles -bool true

> after that, you'll have to restart Finder for the changes to show. You can do this by typing 'killall Finder' into the terminal. Now you should be able to see hidden files.
  * Open the '.profile' file in your home directory and change 'grails-1.3.1' to 'grails-1.3.2'.
  * Now you can close that and hide your hidden files again by doing:
> > defaults write com.apple.finder AppleShowAllFiles -bool false

> and then restarting Finder again.
  * Open the grails-1.3.2 zip file in the downloads folder on the dock, which will unzip it.
  * When you open IntelliJ, right click on Macademia and choose 'Upgrade Grails.' Select a new SDK and find grails-1.3.2 in your downloads folder.  Have it create a global grails library as opposed to a project one. You may want to restart InelliJ again now.
  * Then you can do the tools > run grails target, "upgrade" thing to be safe.


I think that should work. If anyone knows how to do it better or encounters any problems, edit this wiki.