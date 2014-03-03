Introduction
============
This quickstart demonstrates the usage of a rules service in conjunction with a camel service, creating a Content-Based Router.
This quickstart also demonstrates the ability to use MVEL expressions to extract objects (the Widgets from their Boxes) and insert them as facts inside the rules engine.
The drl resource is specified using annotations within the DestinationServiceRules interface, and checks the id of Widgets to determine the destination to set on their Boxes.
Then, a Camel route looks at the destination set on the Box for each Widget, and routes it to the appropriate service (RedService, GreenService, or BlueService).

If you would like to watch the rules execution, uncomment these lines in
src/main/resources/META-INF/switchyard.xml:
<rules:listeners>
    <rules:listener class="org.kie.api.event.rule.DebugAgendaEventListener"/>
    <rules:listener class="org.kie.api.event.rule.DebugRuleRuntimeEventListener"/>
</rules:listeners>

![Rules Camel CBR Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/rules-camel-cbr/rules-camel-cbr.jpg)


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

3. Use the RulesCamelCBRClient class to send a request message to the Dealer service.  The client can be
   run from the command-line using:

        mvn exec:java

You should see the following in the command output:
```
```

4. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)
2. [Rules Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Rules)
3. [Camel Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel)
4. [Bean Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Bean)
