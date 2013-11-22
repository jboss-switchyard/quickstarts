Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by sending 
data through TCP/UDP. All data sent to port 3939 and 3940 should be displayed in AS console.

Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart:

        mvn install -Pdeploy

3. Execute client and send text message:

        mvn exec:java -Pudp

4. Check the server console for output from the service:

    'Default: Hello there <your text>';

To test TCP: 

5. Execute client and send text message:

        mvn exec:java

6. Check the server console for output from the service:

        'Secured: Hello there <your text>';

7. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [TCP UDP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/TCP+UDP)
