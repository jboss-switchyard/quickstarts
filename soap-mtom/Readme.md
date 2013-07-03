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

JBoss AS 7
----------
1. Build the quickstart:

        mvn clean install

2. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh --server-config=standalone.xml

3. Deploy the quickstart

        mvn jboss-as:deploy

4. Open a console window and type

        mvn exec:java

5. You should see the following output
```
BufferedImage@xxxxx: type = 5 ColorModel: #pixelBits = 24 numComponents = 3 color space = java.awt.color.ICC_ColorSpace@xxxxx transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 256 height = 256 #numDataElements 3 dataOff[0] = 2
```
## Further Reading

1. [SOAP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
2. [SOAP Message Transmission Optimization Mechanism (MTOM)] http://www.w3.org/TR/soap12-mtom/
3. [XML-binary Optimized Packaging (XOP)] http://www.w3.org/TR/xop10/
