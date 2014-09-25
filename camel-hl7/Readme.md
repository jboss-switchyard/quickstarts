Introduction
============
This quickstart demonstrates the usage of the Camel HL7 component within a
SwitchYard service.  A tcp endpoint listens for requests and routes them to 
the service, which prints out parts of the request to the console. 

Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Submit a request with test client.

        mvn exec:java

Expected Output
===============
```
12:35:01,274 INFO  [stdout] (pool-7-thread-1) Query Date/Time : 200612211200
12:35:01,274 INFO  [stdout] (pool-7-thread-1) Query Format Code : R
12:35:01,275 INFO  [stdout] (pool-7-thread-1) Query Priority : I
12:35:01,275 INFO  [stdout] (pool-7-thread-1) Query ID : GetPatient
12:35:01,275 INFO  [stdout] (pool-7-thread-1) Deferred Response Type : null
12:35:01,275 INFO  [stdout] (pool-7-thread-1) Deferred Response Date/Time : null
12:35:01,276 INFO  [stdout] (pool-7-thread-1) Quantity Limited Request : 1
12:35:01,276 INFO  [stdout] (pool-7-thread-1) Query Results Level : null
```

4. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [Camel HL7](http://camel.apache.org/hl7.html)
