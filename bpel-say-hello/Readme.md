Introduction
============

This example demonstrates a BPEL process that reads a message passed as input and 
replies to it with a "Hello, &lt;input&gt;" message.

![Say Hello Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/say_hello/bpel-say-hello.jpg)


Running the quickstart
======================


EAP
----------

1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart : 

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
      request.     See the "Expected Output" heading for the expected results.

4. Undeploy the quickstart:

        mvn clean -Pdeploy

Wildfly
=================================

1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart : 

        mvn install -Pdeploy -Pwildfly

3. Submit a webservice request to invoke the SOAP gateway. 
```
        mvn exec:java
```
<br/>

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
=================================

1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the bpel-say-hello quickstart :

karaf@root> features:install features:install switchyard-quickstart-bpel-say-hello

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-bpel-say-hello


Expected Output
===============

```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Header/>
<SOAP-ENV:Body>
<sayHelloResponse xmlns="http://www.jboss.org/bpel/examples">
<tns:result xmlns:tns="http://www.jboss.org/bpel/examples">Hello Fred</tns:result>
</sayHelloResponse>
</SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

## Further Reading

1. [BPEL Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/BPEL)
