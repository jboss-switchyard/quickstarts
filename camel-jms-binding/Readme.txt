Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by binding to a JMS Queue. When a message arrives
in this queue the service will be invoked.
This quickstart can be deployed to JBoss AS 6.x and JBoss AS 7. The deployment proceedures and the configuration between these version differ 
so there are some additional steps required for AS 7.

Running the quickstart
======================

JBoss AS 6
----------
1. To install SwitchYard to AS 6.x
    a) unzip SwitchYard core deployers:
        unzip core/deploy/jboss-as6/build/target/switchyard.deployer.zip -d "${AS6_SERVER}/deployers"
    b) copy SwitchYard Bean Component to the deploy directory:
        cp components/bean/target/switchyard-component-bean-${VERSION}.jar ${AS6_SERVER}/deploy
    c) copy SwitchYard Camel Component to the deploy directory:
        cp components/camel-core/target/switchyard-component-camel-${VERSION}.jar ${AS6_SERVER}/deploy
    d) copy SwitchYard Camel Component to the lib directory:
        cp components/camel-as6/target/switchyard-component-camel-as6-${VERSION}.jar ${AS6_SERVER}/lib
    e) copy Apache Camel jars to the lib directory:
        1. cd component/camel-core
        2. mvn dependency:copy-dependencies
        3. cp target/dependency/camel-* ${AS6_SERVER}/lib
        4. cp target/dependency/commons-management-1.0.jar ${AS6_SERVER}/lib
        5. cp target/dependency/spring-* ${AS6_SERVER}/lib
2. Build the quickstart:
    mvn clean install
3. Deploy the quickstart
    cp target/target/switchyard-quickstarts-camel-jms-binding-0.2.0-SNAPSHOT.jar ${AS6_SERVER}/deploy
6. Execute JMSClient
    mvn exec:java -Dexec.mainClass="org.switchyard.quickstarts.camel.jms.binding.JMSClient"
    

JBoss AS 7
----------
1. To install SwitchYard to AS 7 follow the instructions presented here:
    http://community.jboss.org/wiki/SwitchYardOnAS7
2. Build the quickstart:
    mvn clean install
3. Start JBoss AS 7 in standalone mode:
    ./standalone
4. Start JBoss CLI and connect: 
    ./jboss-admin.sh --connect
5. Create the JMS Queue using CLI:
    add-jms-queue --name=GreetingServiceQueue --entries=GreetingServiceQueue --durable=true                                                           
6. Deploy the quickstart
    cp target/switchyard-quickstarts-camel-jms-binding-0.2.0-SNAPSHOT.jar ${AS7}/standalone/deployments
7. Execute HornetQClient
    mvn exec:java -Dexec.mainClass="org.switchyard.quickstarts.camel.jms.binding.HornetQClient"
8. Check the server console for output from the service.
