Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by sending 
data through TCP/UDP. All data sent to port 3939 and 3940 should be displayed in AS console.

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone-full mode:
    ${AS}/bin/standalone.sh --server-config=standalone-full.xml
3. Deploy the quickstart
    cp target/switchyard-quickstarts-camel-netty-binding.jar ${AS7}/standalone/deployments
4. Execute client and send text message:
    a) for tcp test use: mvn exec:java
    b) for udp test use: mvn exec:java -Pudp
5. Check the server console for output from the service.

## Further Reading

1. [Camel Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Bindings)

