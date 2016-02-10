# Introduction #

Grass is used to compile sass and scss into css. It currently requires semi-manual installation of the plugin (from git), and compass.



# Disable Grass #

If you do not want to bother with grass, you can still have a mostly working system (you will not be able to compile the sass automatically and therefore will need to manually recompile the scss on each change).

If you wish to compile the sass without using the plugin, skip to the [GrassPlugin#Manually\_Without\_Plugin](GrassPlugin#Manually_Without_Plugin.md).

If you wish to use grass, read on.

# Install Needed Programs #

You will need a few things installed first:

### Install git ###

First, find out if you have git installed:
```
which git
```
if not install it:

on a mac with macports:
```
sudo port install git-core
```

without macports
use installer found on git site
< http://git-scm.com/download >

linux:
use your package manager or compile from source

windows:
use installer found on git site
< http://git-scm.com/download >

### Install Ruby ###

find out if you have ruby installed:
```
which ruby
```
if not install it:

Macports (newer macs ship with ruby, but maybe not the older ones...):
```
sudo port install ruby
```

mac w/o macports:
install from site
< http://www.ruby-lang.org/en/downloads/ >

linux:
use package manager or install from source

windows:
install from site
< http://www.ruby-lang.org/en/downloads/ >

### Install Compass ###

find out if you have compass installed:
```
which compass
```
if not, install it
```
gem install compass
```
or use a platform specific installation.

make sure it's in your path:
```
which install compass
```
if not:
add the installation to your PATH.
for a user installed gem on a mac:
```
echo "PATH=$PATH:$HOME/.gem/ruby/1.8/bin/" >> $HOME/.profile
```

linux users: replace `.profile` in the command above with .bashrc

# Install the Plugin #

With any luck, everything should be ready at this point and the rest should be automatic.

run the script from the macademia project root directory
```
./scripts/grass.sh
```
this will:

  1. create a temp directory,
  1. clone the project from git and checkout version 0.3.4,
  1. upgrade the plugin to the current grails version,
  1. package the plugin,
  1. install the plugin

**this script works on MacOSX and the most common flavors of linux.**

**It may or may not work on windows. Cygwin will probably make it work.**

# Compiling #

If everything installed correctly, you can now compile sass/scss.

### Automatic ###

The scss in src/stylesheets should be compiled automatically on run-app, and whenever the files are changed.

### Manually With Plugin ###

To recompile:
```
grails compile-css
```

### Manually Without Plugin ###

Make sure compass and ruby are in your path and you have installed html5-boilerplate. Instructions can be found in the [GrassPlugin#Install\_Needed\_Programs](GrassPlugin#Install_Needed_Programs.md) section above.

In the macademia project root directory run:
```
compass compile --sass-dir ./src/stylesheets --css-dir ./web-app/css --image-dir ./web-app/images --relative-assets true  --output-style compact -r html5-boilerplate
```

# Good Luck #