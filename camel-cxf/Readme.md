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

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode:
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
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <orderResponse xmlns="urn:switchyard-quickstart:camel-cxf:2.0">
            <return>Order Boeing with quantity 10 accepted.</return>
        </orderResponse>
    </soap:Body>
</soap:Envelope>
5. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
2. [Camel-CXF] http://camel.apache.org/cxf.html
