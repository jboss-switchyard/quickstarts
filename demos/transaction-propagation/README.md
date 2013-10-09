# Remote Transaction Propagation Demo Quickstart

This quickstart provides an example of deploying a set of applications demonstrating remote transaction propagation in SwitchYard.  The quickstart consists of the following pieces:

* dealer : contains the Dealer Service which acts as consumer for the Credit Service.
* credit : a remote decision service implemented in Drools.  
* client : the test driver for the application which can be used to send a request payload to the Dealer Service

The purpose of the quickstart is to demonstrate how the transaction initiated by Dealer Service is propagated to the remote Credit Service through SCA binding.

## Running the Example

You will need two SY instances and four terminal windows to run the transaction propagation demo.

*1. Make XTS enabled in standalone-ha.xml.*

In order to achieve remote transaction propagation, XTS is needed to be enabled. Following changeset should be sufficient.
```
--- standalone-ha.xml	2013-10-09 22:09:32.085300978 +0900
+++ standalone-ha-xts.xml	2013-10-16 11:40:57.198147545 +0900
@@ -25,6 +25,7 @@
         <extension module="org.jboss.as.webservices"/>
         <extension module="org.jboss.as.weld"/>
     <extension module="org.switchyard"/>
+        <extension module="org.jboss.as.xts"/>
 </extensions>
     <management>
         <security-realms>
@@ -405,6 +406,9 @@
 <extension identifier="org.apache.camel.soap"/>
 </extensions>
 </subsystem>
+        <subsystem xmlns="urn:jboss:domain:xts:1.0">
+            <xts-environment url="http://${jboss.bind.address:127.0.0.1}:8080/ws-c11/ActivationService"/>
+        </subsystem>
 </profile>
     <interfaces>
         <interface name="management">
```

*2. Create two discrete instances of the SY runtime.*

This can be done by simply making copies of the standalone directory for each instance, e.g.
```
    cd ${EAP_HOME}
    cp -R standalone dealer
    cp -R standalone credit
```

And change the port for the XTS ActivationService you added in standalone-ha.xml so it doesn't conflict. e.g. use 9080 for the credit node.

*3. Start H2 database in TCP server mode.*

_Window 1_

    java -cp modules/system/layers/base/com/h2database/h2/main/h2-1.3.168-redhat-2.jar org.h2.tools.Server -tcp &
    java -cp modules/system/layers/base/com/h2database/h2/main/h2-1.3.168-redhat-2.jar org.h2.tools.Console

H2 console is open at http://localhost:8082/ - Use following JDBC URLs to connect to the databases:

    dealer: jdbc:h2:tcp://localhost/h2db/dealer
    credit: jdbc:h2:tcp://localhost/h2db/credit

username is "sa" and password is "sa".

*4. Start each instance in a separate terminal window.*

Note that you need to specify absolute path for the jboss.server.base.dir so the XTS ActivationService works correctly.

_Window 2_

    bin/standalone.sh -Djboss.node.name=dealer -Djboss.server.base.dir=${EAP_HOME}/dealer --server-config=standalone-ha.xml

_Window 3_

    bin/standalone.sh -Djboss.node.name=credit -Djboss.server.base.dir=${EAP_HOME}/credit -Djboss.socket.binding.port-offset=1000 --server-config=standalone-ha.xml

*5. In a separate terminal window deploy the dealer and credit applications.*

Build the client and service applications

    cd quickstarts/demos/transaction-propagation
    mvn package

Maven plugin should have made DDL files for each of dealer and credit database. Create tables with using these files.

    credit/target/hibernate/create.sql
    dealer/target/hibernate/create.sql

Deploy Credit Service to credit.

    cd credit
    mvn -Djboss-as.port=10999 jboss-as:deploy

Deploy Dealer Service to dealer.

    cd ../dealer
    mvn -Djboss-as.port=9999 jboss-as:deploy

*6. Run the test client and check output.*

Submit a message using the client project:

    cd ../client
    mvn exec:java -Dexec.args="accept"

You should see the following output in the client terminal:

    ==================================
    Was the offer accepted? true
    ==================================

Check the console output on credit to see the message:

    Credit Service : Approving credit for John Smith

If the transaction is committed successfully, database "dealer" and "credit" should have a new record which corresponds the request you submitted. Take a look at the databases from H2 console.

If you submit a message with a parameter "deny":

    mvn exec:java -Dexec.args="deny"

then you should see the following output in the client terminal:

    ==================================
    Was the offer accepted? false
    ==================================

And following output in the dealer console:

    Dealer Service : Low credit score - transaction has been rolled back

In this case, DealerBean sets RollbackOnly flag and the transaction is rolled back. Check the database to see new record is not inserted.

## Further Reading

1. [SwitchYard SCA Binding] (https://docs.jboss.org/author/display/SWITCHYARD/SCA)
2. [SwitchYard Transaction Policy] (https://docs.jboss.org/author/display/SWITCHYARD/Policy)
2. [SwitchYard Clustering](https://docs.jboss.org/author/display/SWITCHYARD/Clustering)
