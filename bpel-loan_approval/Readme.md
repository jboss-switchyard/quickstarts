Introduction
============

This example demonstrates multiple BPEL processes interacting with each other. 
There is a loan approval service that invokes a risk assessment service to help determine whether to 
approve a loan application.


![Loan Approval Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/loan_approval/bpel-loan-approval.jpg)


Running the quickstart
======================

1. Build the quickstart:

        mvn clean install

2. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

3. Deploy the Quickstart :

        mvn jboss-as:deploy

4. Submit a webservice request to invoke the SOAP gateway.  There are a
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
        sample request (src/test/resources/xml/soap-request.xml) as an example of a sample request.  The output 
        below is the expected output :


Expected Output
===============

```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <requestResponse xmlns="http://example.com/loan-approval/loanService/">
         <tns:accept xmlns:tns="http://example.com/loan-approval/loanService/">yes</tns:accept>
      </requestResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

## Further Reading

1. [BPEL Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/BPEL)
