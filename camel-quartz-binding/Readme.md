Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by invoking 
service periodically.

Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy

3. Check the server console for output from the service. By default after every second
   message should be printed

4. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [Quartz Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Quartz)
