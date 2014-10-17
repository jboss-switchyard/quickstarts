Introduction
============
This quickstart demonstrates the usage of the Camel MQTT binding feature. When a message arrives
in the MQTT topic the MQTT service binding receives it and the service will be invoked. Then it
publishes a greeting message to the output MQTT topic through the MQTT reference binding.

![Camel MQTT Binding Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/camel-mqtt-binding/camel-mqtt-binding.jpg)

Running the quickstart
======================


EAP
----------
1. Download a recent version of ActiveMQ (for the purposes of this quickstart, 5.10.0 was tested) and extract the contents. 

2. Start ActiveMQ in standalone mode:

        ${ActiveMQ}/bin/activemq start

3. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

4. Build and deploy the Quickstart :

        mvn install -Pdeploy

5. Use the MqttClient class to send a request message to the queue.  The client can be
   run from the command-line using:

        mvn exec:java

6. Undeploy the quickstart:

        mvn clean -Pdeploy

7. Stop ActiveMQ:

        ${ActiveMQ}/bin/activemq stop


Wildfly
----------
1. Download a recent version of ActiveMQ (for the purposes of this quickstart, 5.10.0 was tested) and extract the contents. 

2. Start ActiveMQ in standalone mode:

${ActiveMQ}/bin/activemq start

3. Start Wildfly in standalone mode:

${AS}/bin/standalone.sh

4. Build and deploy the Quickstart :

mvn install -Pdeploy

5. Use the MqttClient class to send a request message to the queue.  The client can be
run from the command-line using:

mvn exec:java

6. Undeploy the quickstart:

mvn clean -Pdeploy

7. Stop ActiveMQ:

${ActiveMQ}/bin/activemq stop


Karaf
----------
1. Download a recent version of ActiveMQ (for the purposes of this quickstart, 5.10.0 was tested) and extract the contents. 

2. Start ActiveMQ in standalone mode:

${ActiveMQ}/bin/activemq start

1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the camel-mqtt-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-mqtt-binding
5. Use the MqttClient class to send a request message to the queue.  The client can be
run from the command-line using:

mvn exec:java

6. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-mqtt-binding

7. Stop ActiveMQ:

${ActiveMQ}/bin/activemq stop


## Further Reading

1. [Camel XQuery](http://camel.apache.org/mqtt.html)
