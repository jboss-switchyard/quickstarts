Introduction
============
This quickstart demostrates the use of WS-AtomicTransaction in BPEL process which invokes a web service operation requiring WS-AtomicTransaction.

There are 3 scenarios which can be tried:

1) order request + complete request => commit
2) abort request + complete request => rollback caused by one of participants
3) order request + complete request with simulateException set to true => rollback

![BPEL XTS WSAT Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/xts_wsat/bpel-xts-wsat.jpg)

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
      - SOAP-UI : Use the wsdl for this project (bpel/src/main/resources/BusinessTravelArtifacts.wsdl) to create a soap-ui project.
        Use the sample requests from "bpel/src/test/resources/xml" in the way described in Introduction.
