Introduction
============
This quickstart demonstrates the usage of the JCA Component and it's service binding feature,
by binding to a HornetQ Queue. When a message arrives in this queue the service will be invoked.

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone-full mode:
     ./standalone.sh -server-config standalone-full.xml
3. Add JMS user using add-user.sh with username=guest, password=guestp, Realm=ApplicationRealm
    ./add-user.sh
4. Add a guest role to the user "guest"
   echo "" >> ${AS7}/standalone/configuration/application-roles.properties
   echo "guest=guest" >> ${AS7}/standalone/configuration/application-roles.properties
5. Deploy JMS Queue
    cp src/test/resources/switchyard-quickstart-jca-inflow-hornetq-jms.xml ${AS7}/standalone/deployments
6. Deploy the quickstart
    cp target/switchyard-quickstarts-jca-inflow-hornetq.jar ${AS7}/standalone/deployments
7. Execute HornetQClient
    mvn exec:java
8. Check the server console for output from the service.

Expected Results
================
```
21:51:08,132 INFO  [stdout] (Thread-1 (HornetQ-client-global-threads-1584009536)) Hello there Skippy :-)
```

## Further Reading

1. [HornetQ Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JCA+Bindings)
