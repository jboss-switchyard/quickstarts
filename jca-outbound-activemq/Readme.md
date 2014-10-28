Introduction
============
This quickstart demonstrates the usage of the JCA Component and its reference 
binding feature by binding to an ActiveMQ Queue.  When the service is invoked 
the message will  be sent to the queue.   This quickstart also has a service 
binding itself to a ActiveMQ  Queue.  When you send a message into OrderService 
queue, the corresponding  OrderServiceBean#process() will be called.  
OrderServiceBean then will forward it to the ShippingQueue or FillingStockQueue 
through the reference bindings.


EAP
----------
1. Start ActiveMQ broker (quickstart does not embed it)

2. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config standalone-full.xml

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Execute ActiveMQClient

        mvn exec:java

5. Check the output from the client.

6. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start ActiveMQ broker (quickstart does not embed it)

2. Start Wildfly in standalone-full mode:

        ${AS}/bin/standalone.sh -server-config standalone-full.xml

3. Build and deploy the quickstart

        mvn install -Pdeploy -Pwildfly

4. Execute ActiveMQClient

        mvn exec:java

5. Check the output from the client.

6. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
No Karaf feature is present for this quickstart.



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

Running a functional test
=========================

Since this quickstart requires Java EE environment, the functional test
"JCAOutboundBindingTest" does not run by default. You need to execute it manually
by following steps.

1. Build the quickstart:

        mvn clean package

2. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Execute the test:

        mvn test -DskipTests=false

## Further Reading

1. [JCA Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JCA)
