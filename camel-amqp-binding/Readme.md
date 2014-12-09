Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by binding 
to a JMS/AMQP Queue deployed on Apache QPid broker. When a message arrives in this queue the service will be invoked.


Running the quickstart
======================


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

3. Install the feature for the camel-amqp-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-amqp-binding
5. Use the MqttClient class to send a request message to the queue.  The client can be
run from the command-line using:

mvn exec:java

6. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-amqp-binding

7. Stop ActiveMQ:

${ActiveMQ}/bin/activemq stop

## Further Reading

1. [AMQP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/AMQP)
