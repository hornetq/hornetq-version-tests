# Tests compatibility of different HornetQ versions

Runs tests using different HornetQ versions for the client and server.

There are three different modules, and these test different scenarios:

- fail-over
- high-availability
- single-server

To run all tests, just issue

```
export MAVEN_OPTS="-Dcom.sun.management.jmxremote.port=3000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
mvn verify
```

### Failure to find org.hornetq.tests:joram-tests:jar:tests

We reuse tests from the [Joram tests] Maven sub-project of the HornetQ
project.

[Joram tests]: <https://github.com/hornetq/hornetq/tree/master/tests/joram-tests>

Currently this JAR of test code is not deployed to Maven repositories. So in order to run the tests you need to clone [HornetQ's git project] and install this JAR to your local Maven repository.

[HornetQ's git project]: <https://github.com/hornetq/hornetq>

## Changing the versions used

In order to change the versions used, you should make use of the exiting Maven profiles (or create a new profile if necessary).

Select one profile to set the server version and one profile to set the client version. Example:

```
mvn -P 2.3.0-SNAPSHOT-SERVER,2.2.19-CLIENT verify
```

The _default_ profile determines the default versions.
