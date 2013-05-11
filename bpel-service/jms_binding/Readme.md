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
1. Build the quickstart:

        mvn clean install

2. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Add JMS user using add-user.sh with username=guest, password=guestp.1, Realm=ApplicationRealm, role=guest

        ${AS}/bin/add-user.sh

4. Deploy JMS Queue

        cp src/test/resources/switchyard-quickstart-bpel-jms-hornetq-jms.xml ${AS}/standalone/deployments

5. Deploy the quickstart

        mvn jboss-as:deploy

6. Execute HornetQClient

        mvn exec:java

7. Watch the command output for the reply message.
