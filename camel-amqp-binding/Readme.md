Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by binding 
to a JMS/AMQP Queue deployed on Apache QPid broker. When a message arrives in this queue the service will be invoked.


Running the quickstart
======================

The AMQP quickstart works with an embedded Qpid broker inside the unit test, so running the quickstart
is simply a matter of building the project module.

Karaf
----------
1. Build the quickstart

mvn install

2. Execute the QpidServer:

mvn exec:java

3. Start the Karaf server :

${KARAF_HOME}/bin/karaf

4. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

5. Install the feature for the camel-amqp-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-amqp-binding

6. Use the QpidClient class to send a request message to the queue.  The client can be
run from the command-line using:

mvn exec:java -Pclient

7. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-amqp-binding

8. Stop the QpidServer using CTRL^C


## Further Reading

1. [AMQP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/AMQP)
