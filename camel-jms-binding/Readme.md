Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by binding 
to a JMS Queue. When a message arrives in this queue the service will be invoked.

![Camel JMS Binding Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/camel-jms-binding/camel-jms-binding.jpg)


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

        cp src/test/resources/switchyard-quickstart-camel-jms-binding-hornetq-jms.xml ${AS}/standalone/deployments

5. Deploy the quickstart

        mvn jboss-as:deploy

6. Execute HornetQClient

        mvn exec:java

7. Check the server console for output from the service.

## Further Reading

1. [JMS Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JMS)
