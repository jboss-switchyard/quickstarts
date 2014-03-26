Introduction
============
This quickstart demonstrates the usage of the Camel RSSComponent and its binding feature by polling for messages. 

Running the quickstart
======================
Please change connection parameters in src/main/resources/switchyard.xml to point your testing towards different RSS feeds.

- feedUri
- splitEntries
- filter
- feedHeader

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy

5. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [RSS Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/RSS)
