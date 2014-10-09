Introduction
============
This quickstart demonstrates the usage of the camel dataformats. Implemented service accepts
GreetingRequest instances and return GreetingResponse encoded to XML form.

![Camel JAXB Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/camel-jaxb/camel-jaxb.jpg)

Preqrequisites 
==============
Maven

Running the quickstart
======================


EAP
----------
1. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Submit a request with test client. Test application will ask you about name of person to greet

        mvn exec:java

4. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

	${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

	mvn install -Pdeploy -Pwildfly

3. Submit a request with test client. Test application will ask you about name of person to greet

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

3. Install the feature for the camel-ftp-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-ftp-binding

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-ftp-binding



Expected Output:
================
```
Give name for greeting: John
Sending request
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:request xmlns:ns2="urn:switchyard-quickstart:camel-jaxb:1.0">
    <name>John</name>
</ns2:request>

Received response
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:response xmlns:ns2="urn:switchyard-quickstart:camel-jaxb:1.0">
    <greeting>Ola John!</greeting>
</ns2:response>
```



## Further Reading

1. [Camel Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel)
2. [Camel Data Formats](http://camel.apache.org/data-format.html)
