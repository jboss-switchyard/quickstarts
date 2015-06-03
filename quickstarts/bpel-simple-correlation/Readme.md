Introduction
============

This example shows a simple example of how two separate interactions 
can be correlated to the same BPEL process instance.

In this case, a 'hello' message must be sent first, and then a 'goodbye'
message must follow, where both messages must have the same ID field to
enable them to be correlated to the same process instance.

![Simple Correlation Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/simple_correlation/bpel-correlation.jpg)


EAP
=============================

1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Deploy the Quickstart :

        mvn install -Pdeploy

3. Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
    - Submit a request with your preferred SOAP client - src/test/resources/xml contains sample
      requests and the responses that you should see
    - Use the simple bundled SOAP client and the sample request XML e.g.
<br/>
```
        mvn exec:java
```
<br/>
    - SOAP-UI : Use the wsdl for this projects (src/main/resources/wsdl/) to create a soap-ui project.
      Use the sample request (src/test/resources/xml/soap-request.xml) as an example of a sample
      request.   See the "Expected Output" section for the expected results.

4. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
=============================

1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Deploy the Quickstart :

        mvn install -Pdeploy -Pwildfly

3. Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
- Submit a request with your preferred SOAP client - src/test/resources/xml contains sample
requests and the responses that you should see
- Use the simple bundled SOAP client and the sample request XML e.g.
<br/>
```
        mvn exec:java
```
<br/>
- SOAP-UI : Use the wsdl for this projects (src/main/resources/wsdl/) to create a soap-ui project.
Use the sample request (src/test/resources/xml/soap-request.xml) as an example of a sample
request.   See the "Expected Output" section for the expected results.

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
=============================

1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the bpel-simple-correlation quickstart :

karaf@root> features:install switchyard-quickstart-bpel-simple-correlation

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-bpel-simple-correlation



Expected Output
===============

```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <helloMessage xmlns="http://www.jboss.org/bpel/examples/simple_correlation/">
         <sessionId>
            <id>1</id>
         </sessionId>
         <parameter>BPEL, Hello World!</parameter>
      </helloMessage>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

and

```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <goodbyeMessage xmlns="http://www.jboss.org/bpel/examples/simple_correlation/">
         <sessionId>
            <id>1</id>
         </sessionId>
         <parameter>BPEL, Goodbye World!</parameter>
      </goodbyeMessage>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

