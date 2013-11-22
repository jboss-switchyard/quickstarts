Introduction
============
This quickstart demonstrates the usage of the JCA Component and it's service
binding feature, by binding to a ActiveMQ Queue. When a message arrives in this
queue the service will be invoked.  This quickstart is using XPath dynamic
operation selector which determines the service operation to be invoked by
reading message content. If you pass the name and language parameters at step 7
like following:

    mvn exec:java -Dexec.args="Fernando spanish"

then operation selector choose the spanish operation so you would get a spanish greeting like this:

    Hola Fernando :-)

JBoss AS 7
----------
1. Start ActiveMQ broker (quickstart does not embed it)

2. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh -server-config standalone-full.xml

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Execute ActiveMQClient

        mvn exec:java

5. Check the server console for output from the service.

6. Undeploy the quickstart:

        mvn clean -Pdeploy

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

2. Copy the ActiveMQ RAR into AS7 deployment directory:

        cp ${MAVEN_HOME}/repository/org/apache/activemq/activemq-rar/5.8.0/activemq-rar-5.8.0.rar ${AS}/standalone/deployments/activemq-ra.rar

3. Add a resource adapter configuration in standalone-full.xml

        see this document for more information: https://community.jboss.org/wiki/SwitchYardJCAComponentAndActiveMQResourceAdapter

4. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

5. Execute the test:

        mvn test -DskipTests=false

## Further Reading

1. [JCA Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JCA)
