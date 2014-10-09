Introduction
============
This quickstart demonstrates the usage of SOAP with MTOM/XOP. It binds
one SwitchYard service over SOAP/HTTP URL that can be accessed by any SOAP based client.
It also invokes an external Webservice with the passed in Image binary. The highlight
here is that XOP envelopes are recognized and expanded with the help of the flag xopExpand="true".
Since the SOAP Gateway works with SOAPMessages directly the optimization on the service
and reference outbound side is the responsibility of the user. There is transformer that
shows how easily the XOP message can be created and attachments added in implementations,
for example in this via the Camel processors.

```
+-----------------+      +--------------+      +-------------+      +----------------------+
| http://         | ---- | ImageService | ---- | camel:route | ---- | ImageServiceExternal |
+-----------------+      +--------------+      +-------------+      +----------------------+
```

![SOAP with MTOM/XOP Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/soap-mtom/soap-mtom.jpg)

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

4. See the "Expected Output" section for the expected results.

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

4. See the "Expected Output" section for the expected results.

5. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the soap-mtom quickstart :

karaf@root> features:install switchyard-quickstart-soap-mtom

4. To submit a webservice request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-soap-mtom



Expected Output
======================
```
BufferedImage@xxxxx: type = 5 ColorModel: #pixelBits = 24 numComponents = 3 color space = java.awt.color.ICC_ColorSpace@xxxxx transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 256 height = 256 #numDataElements 3 dataOff[0] = 2
```


## Further Reading

1. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
2. [SOAP Message Transmission Optimization Mechanism (MTOM)] http://www.w3.org/TR/soap12-mtom/
3. [XML-binary Optimized Packaging (XOP)] http://www.w3.org/TR/xop10/
