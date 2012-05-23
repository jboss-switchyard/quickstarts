Introduction
============
This quickstart demonstrates the usage of a rules service in conjuction with a camel service, creating a Content-Based Router.
The drl resource is specified using annotations within the DestinationServiceRules interface, and checks the id of Widgets to determine their destination.
Then, a Camel route looks at the destination set on the Widgets, and routes it to the appropriate service (RedService, GreenService, or BlueService).

![Rules Camel CBR Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/rules-camel-cbr/rules-camel-cbr.jpg)


Preqrequisites 
==============
Maven

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Run the test:
    mvn -Dtest=RulesCamelCBRTest test

Expected Output:
================
```
Running org.switchyard.quickstarts.rules.camel.cbr.RulesCamelCBRTest
INFO  [org.switchyard.quickstarts.rules.camel.cbr.RedServiceBean] Red service processing widget with id: FF0000-ABC-123
INFO  [org.switchyard.quickstarts.rules.camel.cbr.GreenServiceBean] Green service processing widget with id: 00FF00-DEF-456
INFO  [org.switchyard.quickstarts.rules.camel.cbr.BlueServiceBean] Blue service processing widget with id: 0000FF-GHI-789
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.312 sec
```

## Further Reading

1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)
2. [Rules Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Rules+Services)
3. [Camel Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Services)
4. [Bean Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Bean+Services)

