Introduction
============
This quickstart demonstrates a Camel service, which allow you to route messages between SwitchYard 
services using Camel routes.    This quickstart contains two SwitchYard services, "JavaDSL" and 
"XMLService".   Messages sent to JavaDSL will be routed to XMLService, which will log the message
line by line.

![Camel Service Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/camel-service/camel-service.jpg)

Preqrequisites 
==============
Maven

Running the quickstart
======================


EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Use the CamelServiceClient class to send a request message to the JavaDSL service.  The client can be
   run from the command-line using:

        mvn exec:java

4. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy -Pwildfly

3. Use the CamelServiceClient class to send a request message to the JavaDSL service.  The client can be
run from the command-line using:

        mvn exec:java

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the camel-service quickstart :

karaf@root> features:install switchyard-quickstart-camel-service

4. Use the CamelServiceClient class to send a request message to the JavaDSL service.  The client can be
run from the command-line using:

mvn exec:java -Pkaraf

6. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-service



Expected Output:
================
There's a lot of camel logging to dig through, but the output you are looking
for looks like the following :

```
13:59:45,306 INFO  [org.apache.camel.impl.DefaultCamelContext] Apache Camel 2.8.0 (CamelContext: camel-4) started in 0.167 seconds  
13:59:45,396 INFO  [org.apache.camel.impl.DefaultCamelContext] Route: route2 started and consuming from: Endpoint[switchyard://XMLService?namespace=urn%3Aswitchyard-quickstart%3Acamel-service%3A0.1.0]  
13:59:45,445 INFO  [route1] Message received in Java DSL Route  
13:59:45,449 INFO  [route1]   
bob: Hello there!
sally: I like cheese
fred: Math makes me sleepy
bob: E pluribus unum
sally: And milk too
bob: Four score and seven years
sally: Actually, any kind of dairy is OK in my book
```
  
```
13:59:45,467 INFO  [route2] Inside XML Camel Route
13:59:45,468 INFO  [route2] [ognl message] 'sally: I like cheese'
13:59:45,469 INFO  [route2] Inside XML Camel Route
13:59:45,469 INFO  [route2] [mvel message] 'sally: And milk too'
13:59:45,470 INFO  [route2] Inside XML Camel Route
13:59:45,470 INFO  [route2] [message] 'sally: Actually, any kind of dairy is OK in my book'
13:59:45,473 INFO  [org.apache.camel.impl.DefaultCamelContext] Apache Camel 2.8.0 (CamelContext:camel-4) is shutting down
```


## Further Reading

1. [Camel Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel)

