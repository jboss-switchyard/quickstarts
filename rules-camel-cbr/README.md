rules-camel-cbr: demonstrates the usage of a rules service in conjunction with a camel service
============================================
Author: SwitchYard Team  
Level: Beginner  
Technologies: SwitchYard, Camel  
Summary: Demonstrates the usage of a rules service in conjunction with a camel service, creating a Content-Based Router. 
Target Product: FSW  
Source: <https://github.com/jboss-switchyard/quickstarts>


What is it?
-----------
This quickstart demonstrates the usage of a rules service in conjunction with a camel service, creating a Content-Based Router.

This quickstart also demonstrates the ability to use MVEL expressions to extract objects (the Widgets from their Boxes) and insert them as facts inside the rules engine.

The drl resource is specified using annotations within the DestinationServiceRules interface, and checks the id of Widgets to determine the destination to set on their Boxes.
Then, a Camel route looks at the destination set on the Box for each Widget, and routes it to the appropriate service (RedService, GreenService, or BlueService).

For more information, see:
1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)
2. [Rules Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Rules)
3. [Camel Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel)
4. [Bean Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Bean)



System requirements
-------------------

Before building and running this quick start you need:

* Maven 3.0.3 or higher
* JDK 1.6 or 1.7
* JBoss AS 7


Build and Deploy the Quickstart
-------------------------

1. Make sure you have once launched the build from `quickstarts` root by running `mvn clean install` in `quickstarts` folder to install quickstart bom in your local repository

2. Change your working directory to `quickstarts/rules-camel-cbr` directory.
* Run `mvn clean install` to build the quickstart.
* Run the test with the following command:
    mvn -Dtest=RulesCamelCBRTest test


JBoss AS 7
----------
1. Build the quickstart:

        mvn clean install

2. Run the test:

        mvn -Dtest=RulesCamelCBRTest test

Use the bundle
-------------------

Successful tests will produce the following output:

```
Running org.switchyard.quickstarts.rules.camel.cbr.RulesCamelCBRTest
INFO  [org.switchyard.quickstarts.rules.camel.cbr.RedServiceBean] Red service processing boxed widget with id: FF0000-ABC-123
INFO  [org.switchyard.quickstarts.rules.camel.cbr.GreenServiceBean] Green service processing boxed widget with id: 00FF00-DEF-456
INFO  [org.switchyard.quickstarts.rules.camel.cbr.BlueServiceBean] Blue service processing boxed widget with id: 0000FF-GHI-789
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.312 sec
```

If you would like to watch the rules execution, uncomment these lines in
src/main/resources/META-INF/switchyard.xml:
```
<listeners>
    <listener class="org.kie.api.event.rule.DebugAgendaEventListener"/>
    <listener class="org.kie.api.event.rule.DebugWorkingMemoryEventListener"/>
</listeners>
```

## Further Reading

1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)
2. [Rules Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Rules)
3. [Camel Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel)
4. [Bean Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Bean)
