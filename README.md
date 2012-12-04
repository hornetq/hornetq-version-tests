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

## Changing the versions used

In order to change the versions used, you can make use of the exiting Maven profiles.

Select one profile to set the server version and one profile to set the client version. Example:

```
mvn -P 2.3.0-SNAPSHOT-SERVER,2.2.19-CLIENT verify
```

The _default_ profile determines the default versions.
