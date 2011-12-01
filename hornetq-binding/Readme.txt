Introduction
============
This quickstart demonstrates the usage of the HornetQ Component and it's binding feature, by binding to a HornetQ Queue. 
When a message arrives in this queue the service will be invoked.

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone mode:
     ./standalone.sh -server-config standalone-preview.xml
3. Start JBoss CLI and connect: 
    ./jboss-admin.sh --connect
4. Create the JMS Queue using CLI:
    add-jms-queue --name=GreetingServiceQueue
5. Deploy the quickstart
     deploy  /path/to/quickstarts/hornetq-binding/target/switchyard-quickstarts-hornetq-binding-{version}-SNAPSHOT.jar
6. Execute HornetQClient
    mvn exec:java 
7. Check the server console for output from the service.
