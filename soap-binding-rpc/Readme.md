Introduction
============
This quickstart demonstrates the usage of SOAP gateway component using RPC style. It binds
one SwitchYard service over SOAP/HTTP URL that can be accessed by any SOAP based client.
It uses RPC/Lit style WSDL and accepts multiple parameter requests and also invokes an
external Webservice with multiple parameters, RPC style. This QS moreover demonstrates the
various aspects of transforming from one form of SOAP request to another.

<pre>
+-----------------+      +--------------+      +-------------+      +----------------------+
| http://         | ---- | HelloService | ---- | camel:route | ---- | HelloWorldWSExternal |
+-----------------+      +--------------+      +-------------+      +----------------------+
</pre>

![SOAP Binding RPC/Lit Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/soap-binding-rpc/soap-binding-rpc.jpg)

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
    mvn exec:java -Dexec.args=Einstein
</pre>
5. You should see the following output
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Header/>
        <SOAP-ENV:Body>
            <ns2:sayHelloResponse xmlns:ns2="urn:switchyard-quickstart:soap-binding-rpc:1.0">
                <return>Hello World Greeting for 'Einstein' in English on a Sunday!</return>
            </ns2:sayHelloResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>

## Further Reading

1. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP+Bindings)
