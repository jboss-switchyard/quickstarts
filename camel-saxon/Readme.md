Introduction
============
This quickstart demonstrates the usage of the Camel XQuery component within a
SwitchYard service. The RoutingService receives SOAP request messages and routes them
to HelloService or GoodbyeService depending on the request message using xquery expression.
If the text node child of the greet node contains "Garfield", then the message is
routed to the GoodbyeService. Otherwise, the message is routed to the HelloService.

This example is invoked through a SOAP gateway binding. 

Running the quickstart
======================

EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Use the CamelSaxonClient class to send a request message to the GreetingService.  The client can be
   run from the command-line using:

        mvn exec:java

   Enter a name "Garfield" or whatever you like.

4. Undeploy the quickstart:

mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

mvn install -Pdeploy -Pwildfly

3. Use the CamelSaxonClient class to send a request message to the GreetingService.  The client can be
run from the command-line using:

mvn exec:java

Enter a name "Garfield" or whatever you like.

4. Undeploy the quickstart:

mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start JBoss AS 7 in standalone mode:

${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

mvn install -Pdeploy

3. Use the CamelSaxonClient class to send a request message to the GreetingService.  The client can be
run from the command-line using:

mvn exec:java -Pkaraf

Enter a name "Garfield" or whatever you like.

4. Undeploy the quickstart:

mvn clean -Pdeploy 


Expected Output
===============
```
00:28:45,022 INFO  [route7] (http-/127.0.0.1:8080-1) GreetingService - message received: <greet xmlns="urn:switchyard-quickstart:camel-saxon:0.1.0">Garfield</greet>
00:28:45,044 INFO  [stdout] (http-/127.0.0.1:8080-1) Goodbye Garfield
00:29:07,597 INFO  [route7] (http-/127.0.0.1:8080-1) GreetingService - message received: <greet xmlns="urn:switchyard-quickstart:camel-saxon:0.1.0">SpongeBob</greet>
00:29:07,606 INFO  [stdout] (http-/127.0.0.1:8080-1) Hello SpongeBob
```


## Further Reading

1. [Camel XQuery](http://camel.apache.org/xquery.html)
