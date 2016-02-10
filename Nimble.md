# Introduction #
Nimble is a plugin available for grails that helps implementing user interfaces for account control. Details can be found here: http://sites.google.com/site/nimbledoc/ but here are some solutions that we've found to problems that we encountered while experimenting with nimble.


## Overriding ##

In order to change/add to nimble's existing methods without changing files in the plugin, you'll need to override them.

### Views ###

You're probably going to want to change things in the views - copy all of the ones from the plugin that aren't in your project into your project's views folder. You may not change all of them, but they reference eachother and seem to have problems if they try to go between the plugin views and your project views.

### Add a method to an existing class ###

The best way to do this without changing the plugin itself is to create a class in your own project with the same name as the one that you want to add a method to. Then have your new class extend the class from the plugin (the extension should be something like 'grails.plugins.nimble.core._ClassName_'). Create your new method and import anything that grails says it wants you to. Or at least anything that it has trouble finding if you run the app.

### Overriding existing methods ###

If you want to change an existing method from a class in the nimble plugin, create a new class in your own project with the same name as the class that you want to change. Have your new class extend the class from the plugin (the extension should be something like 'grails.plugins.nimble.core._ClassName_'). Define a method with the same name as the method that you want to change and grails will use this instead of the method from the nimble plugin. Change/create the method and import elements as necessary. NOTE: 'super' might work, but we haven't figured it out yet.