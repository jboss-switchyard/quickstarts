Introduction
============
This quickstart demonstrates the usage of the Smooks transformer.  The service contained in this 
quickstart - OrderService - creates an OrderAck object based on the information from the Order 
object receives.  The object is then transformed by the Smooks transformer from an OrderAck 
object to a submitOrder XML and then back to a Order Java object.     

![Transform Smooks Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/transform-smooks/transform-smooks.jpg)


Requirements
============
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

        mvn jboss-as:deploy

4. Run the test from Maven :

        mvn -Dtest=ServiceTransformationTest test

## Further Reading

1. [Transformation Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Transformation)
