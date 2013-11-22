Introduction
============
This quickstart demonstrates the usage of the camel dataformats. Implemented service accepts
GreetingRequest instances and return GreetingResponse encoded to XML form.

![Camel JAXB Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/camel-jaxb/camel-jaxb.jpg)

Preqrequisites 
==============
Maven

Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Submit a request with test client. Test application will ask you about name of person to greet

        mvn exec:java

Expected Output:
================
```
Give name for greeting: John
Sending request
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:request xmlns:ns2="urn:switchyard-quickstart:camel-jaxb:1.0">
    <name>John</name>
</ns2:request>

Received response
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:response xmlns:ns2="urn:switchyard-quickstart:camel-jaxb:1.0">
    <greeting>Ola John!</greeting>
</ns2:response>
```

4. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [Camel Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel)
2. [Camel Data Formats](http://camel.apache.org/data-format.html)
