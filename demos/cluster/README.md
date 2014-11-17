# Cluster Demo Quickstart

This quickstart provides an example of deploying a set of applications containing a clustered service in SwitchYard.  The quickstart consists of the following pieces:

* dealer : contains the Dealer Service which acts as consumer for the clustered Credit Service.
* credit : a clustered decision service implemented in Drools.  
* client : the test driver for the application which can be used to send a request payload to the Dealer Service

The purpose of the quickstart is to demonstrate how the credit service can be clustered by deploying to a group of SwitchYard instances.  

## Running the Example

You will need three SY instances and four terminal windows to run the clustering demo.

*1. Create three discrete instances of the SY runtime.*

This can be done by simply making copies of the standalone directory for each instance, e.g.
```
    cd ${EAP_HOME}
    cp -R standalone node1
    cp -R standalone node2
    cp -R standalone node3
```

*2. Start each instance in a separate terminal window.*

_Window 1_

    bin/standalone.sh -Djboss.node.name=node1 --server-config=standalone-ha.xml

_Window 2_

    bin/standalone.sh -Djboss.node.name=node2 -Djboss.socket.binding.port-offset=1000 --server-config=standalone-ha.xml

_Window 3_

    bin/standalone.sh -Djboss.node.name=node3 -Djboss.socket.binding.port-offset=2000 --server-config=standalone-ha.xml

*3. In a separate terminal window deploy the dealer and credit applications.*

Build and deploy the quickstart

    mvn -Pdeploy install

*4. Run the test client and check output.*

Submit a message using the client project:

    mvn exec:java

You should see the following output in the client terminal:

    ==================================
    Was the offer accepted? true
    ==================================

If you submit multiple messages, you'll notice that the requests are split between the two instances where
the Credit Service is deployed.  Check the console output on node2 and node3 to see where messages are being routed:

    Credit Service : Approving credit for John Smith

*5. Undpeloy the quickstart

    mvn -Pdeploy clean


## Further Reading

1. [SwitchYard Clustering](https://docs.jboss.org/author/display/SWITCHYARD/Clustering)
