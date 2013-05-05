Introduction
============
This quickstart demonstrates the usage of SOAP with Attachments (SwA). It binds
one SwitchYard service over SOAP/HTTP URL that can be accessed by any SOAP based client.
It also invokes an external Webservice with the passed in attachment.

<pre>
+-----------------+      +--------------+      +-------------+      +----------------------+
| http://         | ---- | ImageService | ---- | camel:route | ---- | ImageServiceExternal |
+-----------------+      +--------------+      +-------------+      +----------------------+
</pre>

![SOAP with Attachments Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/soap-binding-rpc/soap-attachment.jpg)

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
<pre>
    mvn clean install
</pre>
2. Start JBoss AS 7 in standalone mode:
<pre>
    ${AS}/bin/standalone.sh --server-config=standalone.xml
</pre>
3. Deploy the quickstart
<pre>
    mvn jboss-as:deploy
</pre>
4. Open a console window and type
<pre>
    mvn exec:java
</pre>
5. You should see the following output
<env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
    <env:Header/>
    <env:Body>
        <ns2:echoImageResponse xmlns:ns2="urn:switchyard-quickstart:soap-attachment:1.0">cid:external-switchyard.png</ns2:echoImageResponse>
    </env:Body>
</env:Envelope>

Response attachment: <external-switchyard.png> with content type image/png

## Further Reading

1. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP+Bindings)
2. [SOAP Messages with Attachments] http://www.w3.org/TR/SOAP-attachments
