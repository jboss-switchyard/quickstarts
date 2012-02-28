Introduction
============
This quickstart demonstrates a Camel service, which allow you to route messages between SwitchYard 
services using Camel routes.    This quickstart contains two SwitchYard services, "JavaDSL" and 
"XMLService".   Messages sent to JavaDSL will be routed to XMLService, which will log the message
line by line.

This example is invoked through a SOAP gateway binding.

Preqrequisites 
==============
Maven

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone mode:
    ${AS}/bin/standalone.sh
3. Deploy the Quickstart : 
    cp target/switchyard-quickstart-camel-service.jar ${AS7}/standalone/deployments
4. Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
      - Submit a request with your preferred SOAP client - src/test/resources/xml contains sample 
        requests and the responses that you should see
      - SOAP-UI : Use the wsdl for this project (src/main/resources/wsdl/JavaDSL.wsdl) to create 
        a soap-ui project.    Use the sample request (src/test/resources/xml/soap-request.xml) as an 
        example of a sample request. The output like below will appear in AS7 log.

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
13:59:45,468 INFO  [route2] [message] 'sally: I like cheese'  
13:59:45,469 INFO  [route2] Inside XML Camel Route  
13:59:45,469 INFO  [route2] [message] 'sally: And milk too'  
13:59:45,470 INFO  [route2] Inside XML Camel Route  
13:59:45,470 INFO  [route2] [message] 'sally: Actually, any kind of dairy is OK in my book'  
13:59:45,473 INFO  [org.apache.camel.impl.DefaultCamelContext] Apache Camel 2.8.0 (CamelContext:camel-4) is shutting down
```

## Further Reading

1. [Camel Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Services)

