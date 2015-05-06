Introduction
============
This quickstart demonstrates how policy can be used to control the transactional characteristics
of a service invocation.  This application contains 4 Bean services, called "WorkService",
"TaskAService", "TaskBService" and "TaskCService. The WorkService accepts commands and dispatch
to other 3 services.
TaskAService expects the global transaction to be propagated, and accepts the "rollback.A" command
to set rollback only flag on that global transaction.
TaskBService requires local transaction, so it suspends the propagated transaction and create new
one. It accepts the "rollback.B" command to set rollback only flang on its local transaction.
TaskCService requires no managed transaction, so it suspends the propagated transaction and run
without managed transaction - i.e. this service never has a transaction to rollback, although it
accepts "rollback.C" command. It simply prints the message saying no transaction when it gets that
command.

![Policy Transaction Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/demos/policy-transaction/policy-transaction.jpg)


Running the quickstart
======================

EAP
----------

1. Start  EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Create an application user:

        ${AS}/bin/add-user.sh -a --user guest --password guestp.1 --group guest

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Execute HornetQClient

        mvn exec:java

5. Check the server console for output from the service.  With the default
   configuration of the quickstart, you should see the output below in the
   AS server.log.

6. Undeploy the application

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Create an application user:

        ${AS}/bin/add-user.sh -a --user guest --password guestp.1 --group guest

3. Build and deploy the quickstart

        mvn install -Pdeploy,wildfly

4. Execute HornetQClient

        mvn exec:java -Pwildfly

5. Check the server console for output from the service.  With the default
   configuration of the quickstart, you should see the output below in the
   AS server.log.

6. Undeploy the application

        mvn clean -Pdeploy,wildfly


Karaf
----------
1. Start the Karaf server :

        ${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the policy-transaction demo :

karaf@root> features:install switchyard-demo-policy-transaction

4. Execute JMSClient

        mvn exec:java -Dexec.args="activemq"

5. Check the server console for output from the service.  With the default
   configuration of the quickstart, you should see the output below in the
   karaf console.

6. Undeploy the demo:

karaf@root> features:uninstall switchyard-demo-policy-transaction


CONSOLE
----------
```
:: WorkService :: Received command =>  rollback.A
:: TaskAService :: Received command =>  rollback.A
:: TaskAService :: Marked transaction to rollback!
:: TaskBService :: Received command =>  rollback.A
:: TaskCService :: Received command =>  rollback.A
:: TaskCService :: No active transaction
:: WorkService :: transaction is marked as rollback only
:: WorkService :: Received command =>  rollback.A
:: TaskAService :: Received command =>  rollback.A
:: TaskAService :: Marked transaction to rollback!
:: TaskBService :: Received command =>  rollback.A
:: TaskCService :: Received command =>  rollback.A
:: TaskCService :: No active transaction
:: WorkService :: transaction is marked as rollback only
:: WorkService :: Received command =>  rollback.A
:: TaskAService :: Received command =>  rollback.A
:: TaskAService :: Marked transaction to rollback!
:: TaskBService :: Received command =>  rollback.A
:: TaskCService :: Received command =>  rollback.A
:: TaskCService :: No active transaction
:: WorkService :: transaction is marked as rollback only
:: WorkService :: Received command =>  rollback.A
:: TaskAService :: Received command =>  rollback.A
:: TaskAService :: Rollbacks completed - will be committed
:: TaskBService :: Received command =>  rollback.A
:: TaskCService :: Received command =>  rollback.A
:: TaskCService :: No active transaction
:: WorkService :: transaction will be committed
```

Scenarios
=========
You can test any of these transaction policy scenarios using this quickstart:

1) Global transaction propagated to the bean service.  This is the default
   configuration of the quickstart and requires the TaskAServiceBean to be
   annotated with @Requires(transaction=TransactionPolicy.PROPAGATES_TRANSACTION)
   and messages to be sent to the policyQSTransacted queue.
   
2) Policy violation - transaction required to be propagated, but no transaction
   provided by the gateway.  To exercise this scenario, send a message to the
   policyQSNonTransacted queue with TransactionPolicy.PROPAGATES_TRANSACTION
   on the bean service.

3) Suspend incoming transaction.  Change the WorkServiceBean transaction
   annotation type to TransactionPolicy.SUSPENDS_TRANSACTION and send a
   message to the policyQSTransacted queue with the command "rollback.A".  Check
   the output and note that the rollback does not impact the transaction used
   to receive the JMS message.

4) A variety of Transaction implementation policy. Each of TaskAService,
   TaskBService and TaskCService has different implementation policy.
   You can see those behavior with passing combination of the "rollback.A",
   "rollback.B" and "rollback.C". TaskAService accepts "rollback.A" and
   set rollback only flag on global transaction, so the receiving from JMS
   queue will be retried. TaskBService accepts "rollback.B" and set rollback
   only flag on local transaction, but it doesn't impact the transaction used
   to receive the JMS message. TaskCService accepts "rollback.C", but it doesn't
   have any transaction to rollback, just print a message.


Options
=======
The maven exec goal in this quickstart accepts the following options:

    mvn exec:java -D exec.args="[command] [queueName]"

Running "mvn exec:java" with no options is equivalent to:

    mvn exec:java -Dexec.args="rollback policyQSTransacted"

If the value for 'command' contains the string "rollback", then the bean service
will attempt to rollback an existing transaction.

The value for 'queueName' should be "policyQSTransacted" or
"policyQSNonTransacted".

Notes
=======

o Due to an issue with shutdown processing, server shutdown may take
  more than 2 minutes when this quickstart is deployed.  To avoid this
  problem, undeploy the application before shutting down the server.

Running a functional test
=========================

Since this quickstart requires Java EE environment, the functional test
"JmsBindingTest" does not run by default. You need to execute it manually
by following steps.

1. Build the quickstart:

        mvn clean package

2. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Execute the test:

        mvn test -DskipTests=false
