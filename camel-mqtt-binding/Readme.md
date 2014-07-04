Introduction
============
This quickstart demonstrates the usage of the Camel MQTT binding feature. When a message arrives
in the MQTT topic the MQTT service binding receives it and the service will be invoked. Then it
publishes a greeting message to the output MQTT topic through the MQTT reference binding.

![Camel MQTT Binding Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/camel-mqtt-binding/camel-mqtt-binding.jpg)

Running the quickstart
======================

JBoss AS 7
----------
1. Download ActiveMQ, extract and add MQTT connector to the conf/activemq.xml as following:
```
--- activemq.xml.orig	2014-07-16 20:23:16.172268887 +0900
+++ activemq.xml	2014-07-10 21:33:19.508232503 +0900
@@ -144,6 +144,7 @@
             <!-- DOS protection, limit concurrent connections to 1000 and frame size to 100MB -->
             <transportConnector name="openwire" uri="tcp://0.0.0.0:61616?maximumConnections=1000&amp;wireformat.maxFrameSize=104857600"/>
             <transportConnector name="amqp" uri="amqp://0.0.0.0:5672?maximumConnections=1000&amp;wireformat.maxFrameSize=104857600"/>
+            <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883"/>
         </transportConnectors>
 
         <!-- destroy the spring context on shutdown to stop jetty -->
```

1. Start ActiveMQ in standalone mode:

        ${ActiveMQ}/bin/activemq start

2. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

3. Build and deploy the Quickstart :

        mvn install -Pdeploy

4. Use the MqttClient class to send a request message to the queue.  The client can be
   run from the command-line using:

        mvn exec:java

5. Undeploy the quickstart:

        mvn clean -Pdeploy

6. Stop ActiveMQ:

        ${ActiveMQ}/bin/activemq stop

## Further Reading

1. [Camel XQuery](http://camel.apache.org/mqtt.html)
