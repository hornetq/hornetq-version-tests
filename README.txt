to run the test suite simply run 'mvn verify'.

you can change the hornetq version by setting the properties in the pom.xml.

to run the failover test you need to enable remote mbean connection by:

export MAVEN_OPTS="-Dcom.sun.management.jmxremote.port=3000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

and then run the failover test by:

mvn -Pfailover verify

remember to install the maven-hornetq-plugin first.