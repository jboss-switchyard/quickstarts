Introduction
============
This quickstart demonstrates the use of a JMS binding for a BPEL service.  Also of
note : the Camel JMS binding in the quickstart uses WSDL as it's interface type
instead of Java.


![BPEL JMS Binding Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/jms_binding/bpel-jms-binding.jpg)


Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Add JMS user using add-user.sh with username=guest, password=guestp.1, Realm=ApplicationRealm, role=guest

        ${AS}/bin/add-user.sh

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Execute HornetQClient

        mvn exec:java

Expected Output
===============
```
Message sent. Waiting for reply ...
REPLY: 
<sayHelloResponse xmlns="http://www.jboss.org/bpel/examples">
  <tns:result xmlns:tns="http://www.jboss.org/bpel/examples">Hello Skippy</tns:result>
</sayHelloResponse>
```

5. Undeploy the quickstart:
        mvn clean -Pdeploy