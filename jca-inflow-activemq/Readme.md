Introduction
============
This quickstart demonstrates the usage of the JCA Component and its service
binding feature by binding to a ActiveMQ Queue.  When a message arrives in this
queue the service will be invoked.  This quickstart is using XPath dynamic
operation selector which determines the service operation to be invoked by
reading message content. If you pass the name and language parameters at step 7
like following:

    mvn exec:java -Dexec.args="Fernando spanish"

then operation selector choose the spanish operation so you would get a spanish greeting like this:

    Hola Fernando :-)



EAP
----------
1. Start ActiveMQ broker (quickstart does not embed it)

2. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config standalone-full.xml

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Execute ActiveMQClient

        mvn exec:java

5. Check the server console for output from the service.

6. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start ActiveMQ broker (quickstart does not embed it)

2. Start Wildfly in standalone-full mode:

        ${AS}/bin/standalone.sh -server-config standalone-full.xml

3. Build and deploy the quickstart

        mvn install -Pdeploy -Pwildfly

4. Execute ActiveMQClient

        mvn exec:java

5. Check the server console for output from the service.

6. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
No Karaf feature is present for this quickstart.



Expected Results
================
```
21:51:08,132 INFO  [stdout] (default-threads - 2) Hello there Skippy :-)
```

Running a functional test
=========================

Since this quickstart requires Java EE environment, the functional test
"JCAInflowBindingTest" does not run by default. You need to execute it manually
by following steps.

1. Build the quickstart:

        mvn clean package

2. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Execute the test:

        mvn test -DskipTests=false

## Further Reading

1. [JCA Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JCA)
