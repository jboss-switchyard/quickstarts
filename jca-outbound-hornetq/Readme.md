Introduction
============
This quickstart demonstrates the usage of the JCA Component and it's reference binding feature,
by binding to a HornetQ Queue. When service is involed the message will be sended to the queue.
This quickstart also have service binding to a HornetQ Queue. When you send a message into 
OrderService queue, corresponding OrderServiceBean#process() will be called. And then
OrderServiceBean will forward it to the ShippingQueue or FillingStockQueue through the reference bindings.

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone-full mode:
     ./standalone.sh -server-config standalone-full.xml
3. Add JMS user using add-user.sh with username=guest, password=guestp, Realm=ApplicationRealm
    ./add-user.sh
4. Add a guest role to the user "guest"
   echo "" >> ${AS7}/standalone/configuration/application-roles.properties
   echo "guest=guest" >> ${AS7}/standalone/configuration/application-roles.properties
5. Deploy JMS Queue
    cp src/test/resources/switchyard-quickstart-jca-outbound-hornetq-jms.xml ${AS7}/standalone/deployments
6. Deploy the quickstart
    cp target/switchyard-quickstart-jca-outbound-hornetq.jar ${AS7}/standalone/deployments
7. Execute HornetQClient
    mvn exec:java
8. Check the output from the client.

Expected Results
================
```
[INFO] --- exec-maven-plugin:1.2:java (default-cli) @ switchyard-quickstart-jca-outbound-hornetq ---
* * *  SHIPPING ORDERS  * * *
 - JAM
 - MILK
 - BREAD
 - JAM

* * *  PENDING ORDERS (FILLING STOCK)  * * *
 - PIZZA
 - POTATO
 - TOYODA RUV4 2008 SILVER
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

## Further Reading

1. [HornetQ Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JCA+Bindings)
