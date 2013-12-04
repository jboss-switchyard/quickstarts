Introduction
============
This quickstart shows that it is possible to have a BPEL process (BusinessTravel) requiring WS-BusinessActivity and this BPEL process has to be invoked within distributed transaction. In this example it will be another BPEL process (DefaultBusinessTravel) which creates the distributed transaction automatically.

There are 2 scenarios which can be tried:

1) order request + complete request => partial complete transaction after the first order request and close transaction after the complete request
2) order request + complete compensate request => partial complete transaction after the first order request and compensate transaction after the complete compensate request

![BPEL XTS Subordinate WSBA Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/xts_subordinate_wsba/bpel-xts-subordinate-wsba.jpg)

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:

        mvn clean install

2. Configure JBoss AS7 to enable XTS (https://community.jboss.org/wiki/DistributedTransactionsInRiftSaw3#JBoss_AS7_XTS_Configuration) 
2. Start JBoss AS 7 in standalone-xts mode:

        ${AS}/bin/standalone.sh --server-config=standalone-xts.xml

3. Deploy the BPEL process and the Web service :

        mvn jboss-as:deploy

4. Submit a webservice requests to invoke the SOAP gateway.  There are a number of ways to do this :
      - Submit a request with your preferred SOAP client - bpel/src/test/resources/xml contains sample
        requests and the responses that you should see
      - SOAP-UI : Use the wsdl for this project (bpel/src/main/resources/DefaultBusinessTravelArtifacts.wsdl) to create a soap-ui project.
        Use the sample requests from "bpel/src/test/resources/xml" in the way described in Introduction.
