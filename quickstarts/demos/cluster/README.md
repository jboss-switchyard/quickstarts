# Cluster Demo Quickstart

This quickstart provides an example of deploying a set of applications containing a clustered service in SwitchYard.  The quickstart consists of the following pieces:

* dealer : contains the Dealer Service which acts as consumer for the clustered Credit Service.
* credit : a clustered decision service implemented in Drools.  
* client : the test driver for the application which can be used to send a request payload to the Dealer Service

The purpose of the quickstart is to demonstrate how the credit service can be clustered by deploying to a group of SwitchYard instances.  

## Running the Example

You will need three SY instances and four terminal windows to run the clustering demo.

EAP
----------
*1. Create three discrete instances of the SY runtime.*

This can be done by simply making copies of the standalone directory for each instance, e.g.
```
    cd ${EAP_HOME}
    cp -R standalone node1
    cp -R standalone node2
    cp -R standalone node3
```
Replace ${EAP_HOME} with the actual path.

*2. Start each instance in a separate terminal window.*

_Window 1_

    bin/standalone.sh -Djboss.node.name=node1 -Djboss.server.base.dir=${EAP_HOME}/node1 --server-config=standalone-ha.xml

_Window 2_

    bin/standalone.sh -Djboss.node.name=node2 -Djboss.server.base.dir=${EAP_HOME}/node2 -Djboss.socket.binding.port-offset=1000 --server-config=standalone-ha.xml

_Window 3_

    bin/standalone.sh -Djboss.node.name=node3 -Djboss.server.base.dir=${EAP_HOME}/node3 -Djboss.socket.binding.port-offset=2000 --server-config=standalone-ha.xml

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


Wildfly
----------
*1. Create three discrete instances of the SY runtime.*

This can be done by simply making copies of the standalone directory for each instance, e.g.
```
    cd ${WILDFLY_HOME}
    cp -R standalone node1
    cp -R standalone node2
    cp -R standalone node3
```
Replace ${WILDFLY_HOME} with the actual path.

*2. Start each instance in a separate terminal window.*

_Window 1_

    bin/standalone.sh -Djboss.node.name=node1 -Djboss.server.base.dir=${WILDFLY_HOME}/node1 --server-config=standalone-ha.xml

_Window 2_

    bin/standalone.sh -Djboss.node.name=node2 -Djboss.server.base.dir=${WILDFLY_HOME}/node2 -Djboss.socket.binding.port-offset=1000 --server-config=standalone-ha.xml

_Window 3_

    bin/standalone.sh -Djboss.node.name=node3 -Djboss.server.base.dir=${WILDFLY_HOME}/node3 -Djboss.socket.binding.port-offset=2000 --server-config=standalone-ha.xml

*3. In a separate terminal window deploy the dealer and credit applications.*

Build and deploy the quickstart

    mvn -Pdeploy,wildfly install

*4. Run the test client and check output.*

Submit a message using the client project:

    mvn exec:java

You should see the following output in the client terminal:

    ==================================
    Was the offer accepted? true
    ==================================

If you submit multiple messages, you'll notice that the requests are split between the two instances where
the Credit Service is deployed.  Check the log on node2 and node3 to see where messages are being routed:

    Credit Service : Approving credit for John Smith

*5. Undpeloy the quickstart

    mvn -Pdeploy,wildfly clean


Karaf
----------
*1. Start the Karaf server*

    ${KARAF_HOME}/bin/karaf

*2. Create three karaf subinstances.*

    karaf@root> admin:create node3
    karaf@root> admin:create node2
    karaf@root> admin:create node1

*3. Change HTTP port for each subinstances.*
Create following 2 files:

${KARAF_HOME}/instances/node2/etc/org.ops4j.pax.web.cfg:
```
org.osgi.service.http.port=9181
```

${KARAF_HOME}/instances/node3/etc/org.ops4j.pax.web.cfg:
```
org.osgi.service.http.port=10181
```

If your machine is multihomed, the default JGroups settings may not work for you.  In that case, uncomment the "jgroups-config" property in the ${KARAF_HOME}/instances/node{1,2,3}/etc/org.switchyard.component.sca.cfg files and create your own JGroups setting files.   We suggest storing them in ${KARAF_HOME}/instances/node{1,2,3}/etc/jgroups-udp-switchyard.xml 

The default jgroups-udp.xml can be found in infinispan-core-${version.jar}, and add the "bind_addr" attribute to the <UDP/> in the default jgroups-udp.xml. The default jgroups-udp.xml is in infinispan-core-${version}.jar.

   <UDP
+        bind_addr="127.0.0.1"
         mcast_addr="${jgroups.udp.mcast_addr:228.6.7.8}"
         mcast_port="${jgroups.udp.mcast_port:46655}"
         tos="8"

*4. Start karaf subinstances.*

    karaf@root> admin:start node1
    karaf@root> admin:start node2
    karaf@root> admin:start node3

*5. Install Credit application on node2 and node3. *
Replace {SWITCHYARD-VERSION} with the version of SwitchYard that you are using (ex. 2.0.0)

    karaf@root> admin:connect node3
    karaf@node3> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features
    karaf@node3> features:install switchyard-demo-cluster-credit
    karaf@node3> logout
    karaf@root> admin:connect node2
    karaf@node2> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features
    karaf@node2> features:install switchyard-demo-cluster-credit
    karaf@node2> logout
    karaf@root>

*6. Install Dealer application on node1.*

    karaf@root> admin:connect node1
    karaf@node1> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features
    karaf@node1> features:install switchyard-demo-cluster-dealer
    karaf@node1> logout
    karaf@root>

*7. Run the test client and check output.*

Submit a message using the client project:

    mvn -Pkaraf exec:java

You should see the following output in the client terminal:

    ==================================
    Was the offer accepted? true
    ==================================

If you submit multiple messages, you'll notice that the requests are split between the two instances where
the Credit Service is deployed.  Check the log on node2 and node3 to see where messages are being routed:

    Credit Service : Approving credit for John Smith

*8. Uninstall the applications.*

    karaf@root> admin:connect node1
    karaf@node1> features:uninstall switchyard-demo-cluster-dealer
    karaf@node1> logout
    karaf@root> admin:connect node2
    karaf@node2> features:uninstall switchyard-demo-cluster-credit
    karaf@node2> logout
    karaf@root> admin:connect node3
    karaf@node3> features:uninstall switchyard-demo-cluster-credit
    karaf@node3> logout
    karaf@root> 

## Further Reading

1. [SwitchYard Clustering](https://docs.jboss.org/author/display/SWITCHYARD/Clustering)
