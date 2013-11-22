Introduction
============
This quickstart demonstrates the usage of SOAP gateway component using RPC style. It binds
one SwitchYard service over SOAP/HTTP URL that can be accessed by any SOAP based client.
It uses RPC/Lit style WSDL and accepts multiple parameter requests and also invokes an
external Webservice with multiple parameters, RPC style. This QS moreover demonstrates the
various aspects of transforming from one form of SOAP request to another.

```
+-----------------+      +--------------+      +-------------+      +----------------------+
| http://         | ---- | HelloService | ---- | camel:route | ---- | HelloWorldWSExternal |
+-----------------+      +--------------+      +-------------+      +----------------------+
```

![SOAP Binding RPC/Lit Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/soap-binding-rpc/soap-binding-rpc.jpg)

Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh --server-config=standalone.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy

3. Open a console window and type

        mvn exec:java -Dexec.args=Einstein

4. You should see the following output
```
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Header/>
        <SOAP-ENV:Body>
            <ns2:sayHelloResponse xmlns:ns2="urn:switchyard-quickstart:soap-binding-rpc:1.0">
                <return>Hello World Greeting for 'Einstein' in English on a Sunday!</return>
            </ns2:sayHelloResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>
```

5. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
