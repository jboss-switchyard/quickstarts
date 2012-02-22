# MultiApp Demo - Order Consumer

The MultiApp order-consumer project provides a simple route from a JMS endpoint to a SOAP endpoint provided by OrderService (see order-service).  Use this project to submit orders using a JMS message.

## Running the OrderService application

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone-full mode:
    ${AS}/bin/standalone.sh --server-config=standalone-full.xml
3. Start JBoss CLI and connect: 
    ./jboss-admin.sh --connect
4. Create the JMS Queue using CLI:
    add-jms-queue --name=OrderRequestQueue --entries=OrderRequestQueue  
    add-jms-queue --name=OrderReplyQueue --entries=OrderReplyQueue                                                         
5. Deploy the quickstart
    cp target/switchyard-quickstart-demo-multi-order-consumer.jar ${AS7}/standalone/deployments
6. Execute the test client
    mvn exec:java
7. Check the server console for output from the service.

