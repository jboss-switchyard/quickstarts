Introduction
============
This quickstart demonstrates the usage of SOAP with Camel Cxf component. It binds
a SwitchYard service over Cxf URL and invokes that using a Cxf URL.

<pre>
+------------+      +--------------+      +-------------+      +--------------------------+      +-----------+      +------------------+
| http://    | ---- | OrderService | ---- | camel:route | ---- | WarehouseServiceExternal | ---- | cxf://url | ---- | WarehouseService |
+------------+      +--------------+      +-------------+      +--------------------------+      +-----------+      +------------------+
</pre>

Running the quickstart
======================


EAP
----------
1. Start EAP in standalone mode:
<pre>
    ${AS}/bin/standalone.sh --server-config=standalone.xml
</pre>
2. Build and deploy the quickstart
<pre>
    mvn install -Pdeploy
</pre>
3. Open a console window and type
<pre>
    mvn exec:java -Dexec.args="Boeing 10"
</pre>
4. You should see the following output

5. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:
<pre>
${AS}/bin/standalone.sh --server-config=standalone.xml
</pre>
2. Build and deploy the quickstart
<pre>
mvn install -Pdeploy -Pwildfly
</pre>
3. Open a console window and type
<pre>
mvn exec:java -Dexec.args="Boeing 10"
</pre>
4. You should see the following output

5. Undeploy the quickstart:

mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the quickstart feature :

karaf@root> features:install switchyard-quickstart-camel-cxf

3. Open a console window and type
<pre>
mvn exec:java -Pkaraf
</pre>

4. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-cxf


Expected Output
=====================
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
<soap:Body>
<orderResponse xmlns="urn:switchyard-quickstart:camel-cxf:2.0">
<return>Order Boeing with quantity 10 accepted.</return>
</orderResponse>
</soap:Body>
</soap:Envelope>


## Further Reading

1. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
2. [Camel-CXF] http://camel.apache.org/cxf.html
