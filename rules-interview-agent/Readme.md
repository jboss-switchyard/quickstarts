Introduction
============
This quickstart demonstrates the usage of a rules service which performs an age check.  The 
package resource is specified using annotations within the InterviewRules interface.       

Preqrequisites 
==============
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
    cp target/switchyard-quickstart-rules-interview-agent.jar ${AS7}/standalone/deployments
4. Submit a webservice request to invoke the SOAP gateway.  There are a number of ways to do this :
      - Submit a request with your preferred SOAP client - src/test/resources/xml contains sample 
        requests and the responses that you should see
      - SOAP-UI : Use the wsdl for this project (src/main/resources/wsdl/Interview.wsdl) to create 
        a soap-ui project.    Use the sample request (src/test/resources/xml/soap-request.xml) as an 
        example of a sample request. The output like below will appear in AS7 log.

Expected Output:
================
```
16:16:34,693 INFO  [stdout] (default-workqueue-1) ********** David is a valid applicant. **********
```

## Further Reading

1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)

