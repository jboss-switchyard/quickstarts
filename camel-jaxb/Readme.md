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
1. Build the quickstart:

        mvn clean install

2. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

3. Deploy the Quickstart :

        mvn jboss-as:deploy

4. Submit a request with test client. Test application will ask you about name of person to greet

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


## Further Reading

1. [Camel Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Bindings)
2. [Camel Data Formats](http://camel.apache.org/data-format.html)
