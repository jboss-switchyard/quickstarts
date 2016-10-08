Introduction
============
This quickstart demonstrates the usage of the HornetQ Component and it's binding feature, by binding to a HornetQ Queue. 
When a message arrives in this queue the service will be invoked.

JBoss AS 7
----------
1. To install SwitchYard to AS 7 follow the instructions presented here:
    http://community.jboss.org/wiki/SwitchYardOnAS7
    Make sure that you update standalone-preview.xml as this quickstart requires the messaging subsystem.
2. Build the quickstart:
    mvn clean install
3. Start JBoss AS 7 in standalone mode:
     ./standalone.sh -server-config standalone-preview.xml
4. Start JBoss CLI and connect: 
    ./jboss-admin.sh --connect
5. Create the JMS Queue using CLI:
    add-jms-queue --name=GreetingServiceQueue
6. Deploy the quickstart
     deploy  /path/to/quickstarts/hornetq-binding/target/switchyard-quickstarts-hornetq-binding-0.2.0-SNAPSHOT.jar
7. Execute HornetQClient
    mvn exec:java 
8. Check the server console for output from the service.
