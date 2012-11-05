Runs tests using different HornetQ versions for the client and server.

There are three categories of test here:

1. Simple test suite. In this category a single hornetq server will be started before the tests and shut down after.

to run the test suite simply run `mvn -Pdefault verify`.

2. Failover tests. In this tests a single hornetq server and its backup will be started before the tests and shut down after.

to run the failover test you need to first enable remote mbean connection by:

`export MAVEN_OPTS="-Dcom.sun.management.jmxremote.port=3000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"`

then run

`mvn -Pfailover verify`

3. HA tests. In this tests 5 hornetq servers are started before the tests and shut down after.

You also need to enable jmx by:

`export MAVEN_OPTS="-Dcom.sun.management.jmxremote.port=3000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"`

then run

`mvn -Pha verify`

All HA test class names should be of the pattern *HAT.java.

you can change the hornetq version by setting the properties in the pom.xml.

`mvn -Pha verify`

## Requirements

Remember to install the [maven-hornetq-plugin] first.
[maven-hornetq-plugin]: https://github.com/hornetq/maven-hornetq-plugin
