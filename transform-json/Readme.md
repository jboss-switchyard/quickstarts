Introduction
============
This quickstart demonstrates the usage of the JSON transformer.  The service, OrderService, 
creates and returns an OrderAck object based on information from the Order object it receives.
The JSON transformer then transforms the OrderAck to a JSON order object back into a Java Order 
object.

Prerequisites
=============
Maven

Running the quickstart
======================

1. Build the quickstart:
    mvn clean install
2. Run the test:
    mvn -Dtest=JsonTransformationTest test

## Further Reading

1. [Transformation Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Transformation)

