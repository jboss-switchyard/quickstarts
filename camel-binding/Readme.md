Introduction
============
This quickstart demonstrates the usage of a camel binding.   This example shows a GreetingService 
which prints a hello message.  The camel binding   redirects a textfile as input for the 
GreetingServices' hello message.  

![Camel Binding Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/camel-binding/camel-binding.jpg)


Preqrequisites 
==============
Maven

Running the quickstart
======================

1. Build the quickstart:
    mvn clean install
2. Run the test:
    mvn -Dtest=CamelBindingTest test

Expected Output:
================
There's a lot of camel logging to dig through, but the output you are looking for looks like the 
following - you should see the "Hello there Captain Crunch :-)" message :

```
13:06:11,232 INFO  [org.apache.camel.impl.DefaultCamelContext] Apache Camel 2.8.0 (CamelContext: camel-2) started in 0.569 seconds  
13:06:11,796 INFO  [org.apache.camel.impl.DefaultCamelContext] Route: {urn:switchyard-quickstart:camel-binding:0.1.0}GreetingService-[file://target/input?fileName=test.txt&initialDelay=50&delete=true] started and consuming from: Endpoint[file://target/input?delete=true&fileName=test.txt&initialDelay=50]  
Hello there Captain Crunch :-)   
13:06:12,323 INFO  [org.apache.camel.impl.DefaultShutdownStrategy] Starting to graceful shutdown 1 routes (timeout 300 seconds)
```

## Further Reading

1. [Camel Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Bindings)

