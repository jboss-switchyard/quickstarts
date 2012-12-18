Introduction
============
This quickstart demonstrates the usage of the HornetQ Component and it's binding feature,
by binding to a HornetQ Queue. When a message arrives in this queue the service will be invoked.

![HornetQ Binding Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/hornetq-binding/hornetq-binding.jpg)


JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone-full mode:
     ./standalone.sh -server-config standalone-full.xml
3. Add JMS user using add-user.sh with username=guest, password=guestp, Realm=ApplicationRealm, role=guest
    ./add-user.sh
5. Deploy JMS Queue
    cp src/test/resources/switchyard-quickstart-hornetq-binding-hornetq-jms.xml ${AS7}/standalone/deployments
6. Deploy the quickstart
    mvn jboss-as:deploy
7. Execute HornetQClient
    mvn exec:java
8. Check the server console for output from the service.

Expected Results
================
```
14:20:30,063 INFO  [org.jboss.as.server.controller]
(DeploymentScanner-threads - 2) Deployed "switchyard-quickstart-hornetq-binding.jar"
14:20:46,268 INFO  [org.switchyard.component.hornetq.deploy.InboundHandler]
(Thread-1 (group:HornetQ-client-global-threads-9176206)) onMessage :ClientMessage[messageID=9, durable=false, address=jms.queue.GreetingServiceQueue,properties=TypedProperties[null]]
14:20:46,280 INFO  [stdout]
(Thread-1 (group:HornetQ-client-global-threads-9176206)) Hello there Captain Crunch :-)
```

## Further Reading

1. [HornetQ Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/HornetQ+Bindings)
