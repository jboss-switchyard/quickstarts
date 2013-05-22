Introduction
============
This quickstart demonstrates the usage of the JCA Component and it's reference binding feature,
by binding to a ActiveMQ Queue. When service is invoked the message will be sent to the queue.
This quickstart also has a service binding to a ActiveMQ Queue. When you send a message into 
OrderService queue, corresponding OrderServiceBean#process() will be called. And then
OrderServiceBean will forward it to the ShippingQueue or FillingStockQueue through the reference bindings.

JBoss AS 7
----------
1. Build the quickstart:

        mvn clean install

2. Deploy ActiveMQ resource adapter:

        https://community.jboss.org/wiki/SwitchYardJCAComponentAndActiveMQResourceAdapter

3. Start ActiveMQ broker (quickstart does not embed it)

4. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh -server-config standalone-full.xml

5. Deploy the quickstart

        mvn jboss-as:deploy

6. Execute ActiveMQClient

        mvn exec:java

7. Check the output from the client.

Expected Results
================
```
[INFO] --- exec-maven-plugin:1.2:java (default-cli) @ switchyard-quickstart-jca-outbound-activemq ---
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

1. [JCA Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JCA+Bindings)
