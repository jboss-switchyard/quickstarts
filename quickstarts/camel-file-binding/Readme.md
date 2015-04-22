Introduction
============
This quickstart demonstrates the usage of a camel file binding and scripting support in camel/
This example shows a GreetingService which prints a hello message. The camel binding redirects a textfile as input for the 
GreetingServices' hello message.

![Camel Binding Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/camel-file-binding/camel-binding.jpg)


Preqrequisites 
==============
Maven

Running the quickstart
======================

EAP
----------

1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart : 

        mvn install -Pdeploy

3. Copy src/test/resources/test.txt to ${AS}/target/input/test.txt

4. Undeploy the quickstart:

        mvn clean -Pdeploy

Wildfly
----------

1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart : 

        mvn install -Pdeploy -Pwildfly

3. Copy src/test/resources/test.txt to ${AS}/target/input/test.txt

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly

Karaf
=================================

1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the camel-file-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-file-binding

4. Copy src/test/resources/test.txt to ${KARAF_HOME}/target/input/test.txt

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-file-binding


Expected Output:
================
There's a lot of camel logging to dig through, but the output you are looking for looks like the 
following - you should see the "Hello there Captain Crunch :-)" message :

```
20:45:11,598 INFO  [stdout] (Camel (camel-6) thread #4 - file://target/input) Hello there Captain Crunch :-) 
```

## Further Reading

1. [File Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/File)
