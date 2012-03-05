Introduction
============
I have hacked the BPEL JMS Binding quickstart to include a JMS gateway binding in addition 
to the SOAP gateway binding.

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone-full mode:
    ${AS}/bin/standalone.sh --server-config=standalone-full.xml
3. Add JMS user using add-user.sh with username=guest, password=guestp, Realm=ApplicationRealm, and role=guest
    ./add-user.sh
3. Start JBoss CLI and connect: 
    ./jboss-admin.sh --connect
4. Create the JMS Queue using CLI:
    jms-queue add --queue-address=HelloRequestQueue --entries=HelloRequestQueue
    jms-queue add --queue-address=HelloReplyQueue --entries=HelloReplyQueue
5. Deploy the quickstart
    cp target/switchyard-quickstart-bpel-service-jms-binding.jar ${AS7}/standalone/deployments
6. Execute HornetQClient
    mvn exec:java -Dexec.mainClass="org.switchyard.quickstarts.bpel.service.jmsbinding.HornetQClient" -Dexec.classpathScope="test" -Dexec.args="Skippy"
7. Watch the command output for the reply message.


