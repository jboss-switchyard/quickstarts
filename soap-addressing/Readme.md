Introduction
============
This quickstart demonstrates the usage of SOAP with WS-Addressing. It binds
three SwitchYard services over SOAP/HTTP URL. There are two flows to this quickstart
as illustrated below. When the client sends a WS-A request, a reply is immediately sent
back to the client from the OrderService. The OrderService has now forwarded the
request to WarehouseService with a ReplyTo address set as ClientService. Once the
WarehouseService completes its processing the response is sent to ClientService.
The client service then again writes the response to a file.

<pre>
+-----------------+      +--------------+      +-------------+      +------------------+
| http://         | ---- | OrderService | ---- | camel:route | ---- | WarehouseService |
+-----------------+      +--------------+      +-------------+      +------------------+

+------------------+      +---------------+      +-------------+
| WarehouseService | ---- | ClientService | ---- | camel:file  |
+------------------+      +---------------+      +-------------+

</pre>

![SOAP with WS-Addressing](https://github.com/jboss-switchyard/quickstarts/raw/master/soap-addressing/soap-addressing.jpg)

Running the quickstart
======================


EAP
----------
1. Start EAP in standalone mode:
<pre>
        ${AS}/bin/standalone.sh --server-config=standalone.xml
</pre>
2. If on Windows, please create a directory called 'tmp' under c:
3. Build and deploy the quickstart
<pre>
        mvn install -Pdeploy
</pre>
4. Open a console window and type
<pre>
        mvn exec:java -Dexec.args="Boeing 10"
</pre>
5. See the "Expected Output" section for the expected results.

6. After a few seconds, this also should be displayed

Order Boeing with quantity 10 accepted.

7. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:
<pre>
        ${AS}/bin/standalone.sh --server-config=standalone.xml
</pre>
2. If on Windows, please create a directory called 'tmp' under c:
3. Build and deploy the quickstart
<pre>
        mvn install -Pdeploy
</pre>
4. Open a console window and type
<pre>
        mvn exec:java -Dexec.args="Boeing 10"
</pre>
5. See the "Expected Output" section for the expected results.

6. After a few seconds, this also should be displayed

Order Boeing with quantity 10 accepted.

7. Undeploy the quickstart:

mvn clean -Pdeploy


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the SOAP addressing quickstart :

karaf@root> features:install switchyard-quickstart-soap-addressing

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-soap-addressing


Expected Output
======================
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Header>
<Action xmlns="http://www.w3.org/2005/08/addressing">urn:switchyard-quickstart:soap-addressing:1.0:OrderService:orderResponse</Action>
<MessageID xmlns="http://www.w3.org/2005/08/addressing"><some-unique-id></MessageID>
<RelatesTo xmlns="http://www.w3.org/2005/08/addressing">uuid:3d3fcbbb-fd43-4118-b40e-62577894f39a</RelatesTo>
</SOAP-ENV:Header>
<SOAP-ENV:Body>
<orderResponse xmlns="urn:switchyard-quickstart:soap-addressing:1.0">
<return>Thank you for your order. You should hear back from our WarehouseService shortly!</return>
</orderResponse>
</SOAP-ENV:Body>
</SOAP-ENV:Envelope>


## Further Reading

1. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
2. [WS-Addressing] http://www.w3.org/standards/techs/wsaddr#w3c_all
