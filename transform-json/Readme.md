Introduction
============
This quickstart demonstrates the usage of the JSON transformer.  The service, OrderService, 
creates and returns an OrderAck object based on information from the Order object it receives.
The JSON transformer then transforms the OrderAck to a JSON order object back into a Java Order 
object.

This example is invoked through a HornetQ gateway binding.

Prerequisites
=============
Maven

Running the quickstart
======================
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone mode:
     ./standalone.sh -server-config standalone-preview.xml
3. Start JBoss CLI and connect: 
    ./jboss-admin.sh --connect
4. Create the JMS Queue using CLI:
    add-jms-queue --name=OrderServiceQueue
    add-jms-queue --name=StoreResponseQueue
5. Deploy the quickstart
     deploy  /path/to/quickstarts/hornetq-binding/target/switchyard-quickstarts-transform-json.jar
6. Execute HornetQClient
    mvn exec:java 

Expected Results
================
```
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building Quickstart : JSON Transformation 0.4.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] >>> exec-maven-plugin:1.2:java (default-cli) @ switchyard-quickstart-transform-json >>>
[INFO] 
[INFO] --- maven-enforcer-plugin:1.0:enforce (enforce-java-version) @ switchyard-quickstart-transform-json ---
[INFO] 
[INFO] --- maven-enforcer-plugin:1.0:enforce (enforce-maven-version) @ switchyard-quickstart-transform-json ---
[INFO] 
[INFO] <<< exec-maven-plugin:1.2:java (default-cli) @ switchyard-quickstart-transform-json <<<
[INFO] 
[INFO] --- exec-maven-plugin:1.2:java (default-cli) @ switchyard-quickstart-transform-json ---
Sent message [ClientMessage[messageID=0, durable=false, address=jms.queue.OrderServiceQueue,properties=TypedProperties[null]]]
Received message [ClientMessage[messageID=16, durable=true, address=jms.queue.StoreResponseQueue,properties=TypedProperties[null]]]
Message body [{"status":"Order Accepted","orderId":"PO-19838-XYZ","accepted":true}]
```



## Further Reading

1. [Transformation Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Transformation)

