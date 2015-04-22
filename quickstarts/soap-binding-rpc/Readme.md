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

EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh --server-config=standalone.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy

3. Open a console window and type

        mvn exec:java -Dexec.args=Einstein

4. Check the "Expected Output" section below for the expected results.

5. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh --server-config=standalone.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy -Pwildfly

3. Open a console window and type

        mvn exec:java -Dexec.args=Einstein

4. Check the "Expected Output" section below for the expected results.

5. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the soap-binding RPC quickstart :

karaf@root> features:install switchyard-quickstart-soap-binding-rpc

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf -Dexec.args=Einstein
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall sswitchyard-quickstart-soap-binding-rpc


Expected Output
======================
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


## Further Reading

1. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
