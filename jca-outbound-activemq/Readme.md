Introduction
============
This quickstart demonstrates the usage of the JCA Component and it's reference binding feature,
by binding to a ActiveMQ Queue. When service is invoked the message will be sent to the queue.
This quickstart also has a service binding to a ActiveMQ Queue. When you send a message into 
OrderService queue, corresponding OrderServiceBean#process() will be called. And then
OrderServiceBean will forward it to the ShippingQueue or FillingStockQueue through the reference bindings.

JBoss AS 7
----------
1. Start ActiveMQ broker (quickstart does not embed it)

2. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh -server-config standalone-full.xml

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Execute ActiveMQClient

        mvn exec:java

5. Check the output from the client.

6. Undeploy the quickstart:

        mvn clean -Pdeploy

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

2. Copy the ActiveMQ RAR into AS7 deployment directory:

        cp ${MAVEN_HOME}/repository/org/apache/activemq/activemq-rar/5.8.0/activemq-rar-5.8.0.rar ${AS}/standalone/deployments/activemq-ra.rar

3. Add a resource adapter configuration in standalone-full.xml

        see this document for more information: https://community.jboss.org/wiki/SwitchYardJCAComponentAndActiveMQResourceAdapter

4. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

5. Execute the test:

        mvn test -DskipTests=false

## Further Reading

1. [JCA Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JCA)
