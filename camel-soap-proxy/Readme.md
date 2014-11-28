Introduction
============
This quickstart demonstrates the usage of a soap-proxy service. A SOAP service is deployed
on the default HTTP port with context / (ReverseService), and a proxy service is started on the same port with another context /proxy to proxy it.

This quickstart contains a number of artifacts: 

* reverse: implementation of the web service that will be proxied
* reverse-service: a WAR used on EAP to deploy the web service
* soap-proxy: the SwitchYard application

A client to submit a request to the proxy can be found in the soap-proxy module.

![Camel SOAP Proxy Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/camel-soap-proxy/camel-soap-proxy.jpg)

Running the quickstart
======================
EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart: 

        mvn install -Pdeploy

3. Using http://localhost:8080/proxy/ReverseService as the endpoint url, submit a request
   with your preferred SOAP client - src/test/resources/xml contains sample requests and
   the responses that you should see. Alternatively use the simple bundled SOAP client and
   the sample request XML found in the soap-proxy subdirectory e.g.

        cd soap-proxy
        mvn exec:java

4. Undeploy the quickstart:

        mvn clean -Pdeploy 


Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart: 

        mvn install -Pdeploy -Pwildfly

3. Using http://localhost:8080/proxy/ReverseService as the endpoint url, submit a request
   with your preferred SOAP client - src/test/resources/xml contains sample requests and
   the responses that you should see. Alternatively use the simple bundled SOAP client and
   the sample request XML found in the soap-proxy subdirectory e.g.

        cd soap-proxy
        mvn exec:java

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

        ${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard. Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

        karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the camel-soap-proxy quickstart:

        karaf@root> features:install switchyard-quickstart-camel-soap-proxy

4. Use the client class to send a request. The client can be run from the command-line using:

        cd soap-proxy
        mvn exec:java -Pkaraf

6. Undeploy the quickstart:

        karaf@root> features:uninstall switchyard-quickstart-camel-soap-proxy


Expected Output:
================
```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <ns2:reverseResponse xmlns:ns2="urn:switchyard-quickstart:camel-soap-proxy:1.0">
         <text>raboof</text>
      </ns2:reverseResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```


## Further Reading

1. [Camel Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel)
2. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
