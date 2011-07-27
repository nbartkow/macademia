#!/bin/bash

macdir="$PWD"
mkdir /tmp/grasstmp 
cd /tmp/grasstmp
git clone git://github.com/lukaszczerpak/grass.git  
cd grass
git checkout grass-0.3.4
echo "#!/usr/bin/expect" > grailsup
echo "spawn grails upgrade" >> grailsup 
echo 'expect "(y, n)"' >> grailsup
echo 'send "y\r"' >> grailsup
echo 'expect eof' >> grailsup
chmod +x grailsup
./grailsup
rm grailsup
grails package-plugin 
cd $macdir
grails install-plugin /tmp/grasstmp/grass/grails-grass-0.3.4.zip
rm -rf /tmp/grasstmp
