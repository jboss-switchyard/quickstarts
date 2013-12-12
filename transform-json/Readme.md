Introduction
============
This quickstart demonstrates the usage of the JSON transformer.  The service, OrderService, 
creates and returns an OrderAck object based on information from the Order object it receives.
The JSON transformer then transforms the OrderAck to a JSON order object back into a Java Order 
object.

![Transform JSON Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/transform-json/transform-json.jpg)


Prerequisites
=============
Maven

Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Use the JsonTransformationClient class to send a request message to the OrderService.  The client can be run from the command-line using:

        mvn exec:java

You should see the following in the command output:
```
    ==================================
    Was the offer accepted? true
    ==================================
```

4. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [Transformation Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Transformation)
2. [JSON Transformer Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JSON+Transformer)
