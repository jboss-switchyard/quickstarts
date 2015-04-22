Introduction
============
This quickstart shows that it is possible to have a BPEL process (BusinessTravel) requiring WS-BusinessActivity and this BPEL process has to be invoked within distributed transaction. In this example it will be another BPEL process (DefaultBusinessTravel) which creates the distributed transaction automatically.

There are 2 scenarios which can be tried:

1) order request + complete request => partial complete transaction after the first order request and close transaction after the complete request
2) order request + complete compensate request => partial complete transaction after the first order request and compensate transaction after the complete compensate request

![BPEL XTS Subordinate WSBA Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/xts_subordinate_wsba/bpel-xts-subordinate-wsba.jpg)

Running the quickstart
======================

EAP
----------
1. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy

3. Execute BPELClient

        mvn exec:java

4. Undeploy the quickstart:
       
        mvn clean -Pdeploy

Wildfly
----------
1. Start Wildfly in standalone-full mode:

${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart

mvn install -Pdeploy -Pwildfly

3. Execute BPELClient

mvn exec:java

4. Undeploy the quickstart:

mvn clean -Pdeploy -Pwildfly


Karaf
----------

No feature has been created


Expected Output
===============

```
SOAP Reply:
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <orderResponseType xmlns="http://www.jboss.org/bpel/examples">
            <id xmlns="">4</id>
            <fltid xmlns="">Prague/Brno/3/18</fltid>
            <return_fltid xmlns="">Brno/Prague/3/23</return_fltid>
        </orderResponseType>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
SOAP Reply:
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <completeResponseType xmlns="http://www.jboss.org/bpel/examples">
            <id xmlns="">4</id>
        </completeResponseType>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

