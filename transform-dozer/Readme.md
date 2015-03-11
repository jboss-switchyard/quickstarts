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


EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Use the DozerTransformationClient class to send a request message to the OrderService.  The client can be run from the command-line using:

        mvn exec:java

Please check the "Expected Output" section below for the expected results.

4. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy -Pwildfly

3. Use the DozerTransformationClient class to send a request message to the OrderService.  The client can be run from the command-line using:

        mvn exec:java

Please check the "Expected Output" section below for the expected results.

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the transform-dozer quickstart :

karaf@root> features:install switchyard-quickstart-transform-dozer

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-transform-dozer


Expected Output
======================
```
==================================
Was the offer accepted? true
Description: Order Accepted
Order ID: PO-19838-XYZ
==================================
```


## Further Reading

1. [Transformation Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Transformation)
2. [Dozer Transformer Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Dozer+Transformer)
3. [Dozer Documentation](http://dozer.sourceforge.net/)
