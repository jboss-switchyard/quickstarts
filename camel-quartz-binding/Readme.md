Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by invoking 
service periodically.

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone-full mode:
    ${AS}/bin/standalone.sh --server-config=standalone-full.xml
3. Deploy the quickstart
    cp target/switchyard-quickstarts-camel-quartz-binding.jar ${AS7}/standalone/deployments
4. Check the server console for output from the service. By default after every second
   message should be printed

## Further Reading

1. [Camel Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Bindings)

