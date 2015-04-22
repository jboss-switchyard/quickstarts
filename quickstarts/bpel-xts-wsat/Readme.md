Introduction
============
This quickstart demostrates the use of WS-AtomicTransaction in BPEL process which invokes a web service operation requiring WS-AtomicTransaction.

There are 3 scenarios which can be tried:

1. order request + complete request => commit
2. abort request + complete request => rollback caused by one of participants
3. order request + complete request with simulateException set to true => rollback

![BPEL XTS WSAT Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/xts_wsat/bpel-xts-wsat.jpg)

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

No feature has been created.


Expected Output
===============

```
SOAP Reply:
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <orderResponseType xmlns="http://www.jboss.org/bpel/examples">
            <id xmlns="">5</id>
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
            <id xmlns="">5</id>
        </completeResponseType>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```