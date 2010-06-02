# export env variables
export JAVA_HOME=/usr/local/lib64/java/jdk1.6.0_13
export GRAILS_HOME=/opt/grails-1.3.1
export PATH=$PATH:$GRAILS_HOME/bin:/opt/SDK/lib/apache-ant-1.7.1/bin


# cd to project directory within hudson workspace:

cd macademia
echo "classpath is $CLASSPATH; java home is $JAVA_HOME"
env

#rm -f application.properties
#cp -p application.properties.current.txt application.properties

# remove JAVA_OPTS provided by hudson as it may cause errors

export -n JAVA_OPTS
export JAVA_OPTS=-Xmx768m

# now run the grails build / tests

exec ant upgrade war webtest
