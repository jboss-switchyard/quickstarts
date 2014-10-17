Introduction
============
This quickstart demonstrates the usage of the JCA Component and it's reference binding feature,
by binding to a HornetQ Queue. When service is invoked the message will be sent to the queue.
This quickstart also has a service binding to a HornetQ Queue. When you send a message into 
OrderService queue, corresponding OrderServiceBean#process() will be called. And then
OrderServiceBean will forward it to the ShippingQueue or FillingStockQueue through the reference bindings.


EAP
----------
1. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh -server-config standalone-full.xml

2. Add JMS user using add-user.sh with username=guest, password=guestp.1, Realm=ApplicationRealm, role=guest

        ${AS}/bin/add-user.sh

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Execute HornetQClient

        mvn exec:java

5. Check the output from the client.

6. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone-full mode:

${AS}/bin/standalone.sh -server-config standalone-full.xml

2. Add JMS user using add-user.sh with username=guest, password=guestp.1, Realm=ApplicationRealm, role=guest

${AS}/bin/add-user.sh

3. Build and deploy the quickstart

mvn install -Pdeploy

4. Execute HornetQClient

mvn exec:java

5. Check the output from the client.

6. Undeploy the quickstart:

mvn clean -Pdeploy


Karaf
----------
No Karaf feature is present for this quickstart.



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
