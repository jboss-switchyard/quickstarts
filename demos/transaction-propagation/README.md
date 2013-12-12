# Remote Transaction Propagation Demo Quickstart

This quickstart provides an example of deploying a set of applications demonstrating remote transaction propagation in SwitchYard.  The quickstart consists of the following pieces:

* dealer : contains the Dealer Service which acts as consumer for the Credit Service.
* credit : a remote decision service implemented in Drools.  
* client : the test driver for the application which can be used to send a request payload to the Dealer Service

The purpose of the quickstart is to demonstrate how the transaction initiated by Dealer Service is propagated to the remote Credit Service through SCA binding.

## Running the Example

You will need two SY instances and four terminal windows to run the transaction propagation demo.

*1. Create two discrete instances of the SY runtime.*

This can be done by simply making copies of the standalone directory for each instance, e.g.
```
    cd ${EAP_HOME}
    cp -R standalone dealer
    cp -R standalone credit
```

*2. Start each instance in a separate terminal window.*

Note that you need to specify absolute path for the jboss.server.base.dir so the XTS ActivationService works correctly.

_Window 1_

    bin/standalone.sh -Djboss.node.name=dealer -Djboss.server.base.dir=${EAP_HOME}/dealer --server-config=standalone-ha.xml

_Window 2_

    bin/standalone.sh -Djboss.node.name=credit -Djboss.server.base.dir=${EAP_HOME}/credit -Djboss.socket.binding.port-offset=1000 --server-config=standalone-ha.xml

*3. Deploy and build the dealer and credit applications.*

    mvn -Pdeploy install

*4. Run the test client and check output.*

Submit a message using the client project:

    mvn exec:java

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

*5. Stop the database and undeploy the applications

    mvn -Pdeploy clean

## Further Reading

1. [SwitchYard SCA Binding] (https://docs.jboss.org/author/display/SWITCHYARD/SCA)
2. [SwitchYard Transaction Policy] (https://docs.jboss.org/author/display/SWITCHYARD/Policy)
2. [SwitchYard Clustering](https://docs.jboss.org/author/display/SWITCHYARD/Clustering)
