Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by binding to a JMS Queue. When a message arrives in this queue the service will be invoked.

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone mode:
    ./standalone
3. Start JBoss CLI and connect: 
    ./jboss-admin.sh --connect
4. Create the JMS Queue using CLI:
    add-jms-queue --name=GreetingServiceQueue --entries=GreetingServiceQueue --durable=true                                                           
5. Deploy the quickstart
    cp target/switchyard-quickstarts-camel-jms-binding-{version}.jar ${AS7}/standalone/deployments
6. Execute HornetQClient
    mvn exec:java -Dexec.mainClass="org.switchyard.quickstarts.camel.jms.binding.HornetQClient"
7. Check the server console for output from the service.

## Further Reading

1. [Camel Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Bindings)

