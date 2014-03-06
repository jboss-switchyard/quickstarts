Introduction
============
This quickstart demonstrates the usage of the Dozer transformer.  The service, OrderService, 
returns an OrderAck object based on information from the Order object it receives.
This service also demonstrates multi-hop transformation. When a request is submitted,
The JSON transformer transforms JSON order object to a Java Order object, and then
Dozer transformer transforms Java Order object to a Java OrderDomain object.
OrderServiceBean receives the OrderDomain object and set some attributes, then return it.
Before it is returned to the consumer, Dozer transformer transforms returned Java OrderDomain
object to a Java OrderAck object, and finally JSON transformer transforms the OrderAck object
to a JSON orderAck object.

![Transform Dozer Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/transform-dozer/transform-dozer.jpg)


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

3. Use the DozerTransformationClient class to send a request message to the OrderService.  The client can be run from the command-line using:

        mvn exec:java

You should see the following in the command output:
```
    ==================================
    Was the offer accepted? true
    Description: Order Accepted
    Order ID: PO-19838-XYZ
    ==================================
```

4. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [Transformation Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Transformation)
2. [Dozer Transformer Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Dozer+Transformer)
3. [Dozer Documentation](http://dozer.sourceforge.net/)
