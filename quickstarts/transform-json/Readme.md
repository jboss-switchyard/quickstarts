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

EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Use the JsonTransformationClient class to send a request message to the OrderService.  The client can be run from the command-line using:

        mvn exec:java

Check the "Expected Output" section below for the expected results.

4. Undeploy the quickstart:

        mvn clean -Pdeploy



Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy -Pwildfly

3. Use the JsonTransformationClient class to send a request message to the OrderService.  The client can be run from the command-line using:

        mvn exec:java

Check the "Expected Output" section below for the expected results.

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the transform-json quickstart :

karaf@root> features:install switchyard-quickstart-transform-json

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-transform-json



Expected Output
======================
```
==================================
Was the offer accepted? true
==================================
```



## Further Reading

1. [Transformation Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Transformation)
2. [JSON Transformer Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JSON+Transformer)
