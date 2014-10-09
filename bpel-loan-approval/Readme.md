Introduction
============

This example demonstrates multiple BPEL processes interacting with each other. 
There is a loan approval service that invokes a risk assessment service to help determine whether to 
approve a loan application.


![Loan Approval Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/loan_approval/bpel-loan-approval.jpg)


EAP
======================

1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Submit a webservice request to invoke the SOAP gateway.  There are a
   number of ways to do this :
      - Submit a request with your preferred SOAP client - src/test/resources/xml contains sample requests 
        and the responses that you should see
      - Use the simple bundled SOAP client and the sample request XML e.g.
<br/>
```
        mvn exec:java
```
<br/>
      - SOAP-UI : Use the wsdl for this projects (src/main/resources/wsdl/) to create a soap-ui project.  Use the 
        sample request (src/test/resources/xml/soap-request.xml) as an example of a sample request.  See the expected results under the "Expected Output heading". 

4. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
======================

1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy -Pwildfly

3. Submit a webservice request to invoke the SOAP gateway.  There are a
number of ways to do this :
- Submit a request with your preferred SOAP client - src/test/resources/xml contains sample requests 
and the responses that you should see
- Use the simple bundled SOAP client and the sample request XML e.g.
<br/>
```
        mvn exec:java
```
<br/>

4. Undeploy the quickstart:
    
        mvn clean -Pwildfly -Pdeploy


Karaf
======================

1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the bpel-loan-approval quickstart :

karaf@root> features:install switchyard-quickstart-bpel-loan-approval

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-bpel-loan-approval


Expected Output
===============

```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
  <SOAP-ENV:Header>
    <ns:conversationId xmlns:ns="http://www.jboss.org/cid">12345</ns:conversationId>
    <ns:exampleHeader xmlns:ns="http://www.jboss.org/header">Outbound</ns:exampleHeader>
  </SOAP-ENV:Header>
  <SOAP-ENV:Body>
    <requestResponse xmlns="http://example.com/loan-approval/loanService/">
        <tns:accept xmlns:tns="http://example.com/loan-approval/loanService/">yes</tns:accept>
    </requestResponse>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```



## Further Reading

1. [BPEL Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/BPEL)
