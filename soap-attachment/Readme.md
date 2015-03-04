Introduction
============
This quickstart demonstrates the usage of SOAP with Attachments (SwA). It binds
one SwitchYard service over SOAP/HTTP URL that can be accessed by any SOAP based client.
It also invokes an external Webservice with the passed in attachment.

```
+-----------------+      +--------------+      +-------------+      +----------------------+
| http://         | ---- | ImageService | ---- | camel:route | ---- | ImageServiceExternal |
+-----------------+      +--------------+      +-------------+      +----------------------+
```

![SOAP with Attachments Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/soap-attachment/soap-attachment.jpg)

Running the quickstart
======================


EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh --server-config=standalone.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy

3. Open a console window and type

        mvn exec:java

4. You should see the following output

Response attachment: <external-switchyard.png> with content type image/png

5. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh --server-config=standalone.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy -Pwildfly

3. Open a console window and type

        mvn exec:java

4. You should see the following output

Response attachment: <external-switchyard.png> with content type image/png

5. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the SOAP attachment quickstart :

karaf@root> features:install switchyard-quickstart-soap-attachment

4. Turn the logging on ImageService to WARN : 

karaf@root> log:set WARN org.apache.services.ImageService

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-soap-attachment


Expected Output
======================
```
<env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
<env:Header/>
<env:Body>
<ns2:echoImageResponse xmlns:ns2="urn:switchyard-quickstart:soap-attachment:1.0">cid:external-switchyard.png</ns2:echoImageResponse>
</env:Body>
</env:Envelope>
```


## Further Reading

1. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
2. [SOAP Messages with Attachments] http://www.w3.org/TR/SOAP-attachments
