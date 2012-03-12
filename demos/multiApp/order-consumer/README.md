# MultiApp Demo - Order Consumer

The MultiApp order-consumer project provides a simple route from a JMS endpoint to a SOAP endpoint provided by OrderService (see order-service).  Use this project to submit orders using a JMS message.

## Running the OrderService application

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone-full mode:
    ${AS}/bin/standalone.sh --server-config=standalone-full.xml
3. Add JMS user using add-user.sh with username=guest, password=guestp, Realm=ApplicationRealm
    ./add-user.sh
4. Add a guest role to the user "guest"
   echo "guest=guest" >> ${AS7}/standalone/configuration/application-roles.properties
5. Deploy JMS Queue
    cp src/test/resources/switchyard-quickstart-demo-multi-order-consumer-hornetq-jms.xml ${AS7}/standalone/deployments
6. Deploy the quickstart
    cp target/switchyard-quickstart-demo-multi-order-consumer.jar ${AS7}/standalone/deployments
7. Execute the test client
    mvn exec:java
8. Check the server console for output from the service.

