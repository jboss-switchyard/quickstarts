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

    bin/standalone.sh -Djboss.node.name=node1 -Djboss.server.base.dir=node1 --server-config=standalone-ha.xml

_Window 2_

    bin/standalone.sh -Djboss.node.name=node2 -Djboss.server.base.dir=node2 -Djboss.socket.binding.port-offset=1000 --server-config=standalone-ha.xml

_Window 3_

    bin/standalone.sh -Djboss.node.name=node3 -Djboss.server.base.dir=node3 -Djboss.socket.binding.port-offset=2000 --server-config=standalone-ha.xml

*3. In a separate terminal window deploy the dealer and credit applications.*

Build the client and service applications

    cd quickstarts/demos/cluster
    mvn package

Deploy Credit Service to node2 and node3.

    cd credit
    mvn -Djboss-as.port=10999 jboss-as:deploy
    mvn -Djboss-as.port=11999 jboss-as:deploy

Deploy Dealer Service to node1.

    cd ../dealer
    mvn -Djboss-as.port=9999 jboss-as:deploy

*4. Run the test client and check output.*

Submit a message using the client project:

    cd ../client
    mvn exec:java

You should see the following output in the client terminal:

    ==================================
    Was the offer accepted? true
    ==================================

If you submit multiple messages, you'll notice that the requests are split between the two instances where
the Credit Service is deployed.  Check the console output on node2 and node3 to see where messages are being routed:

    Credit Service : Approving credit for John Smith


## Further Reading

1. [SwitchYard Clustering](https://docs.jboss.org/author/display/SWITCHYARD/Clustering)
