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

1. Build the quickstart:

        mvn clean install

2. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Add JMS user using add-user.sh with username=guest, password=guestp.1, Realm=ApplicationRealm, role=guest

        ${AS}/bin/add-user.sh

4. Deploy JMS Queue

        cp src/test/resources/switchyard-quickstart-demo-policy-transaction-hornetq-jms.xml ${AS}/standalone/deployments

5. Deploy the quickstart

        mvn jboss-as:deploy

6. Execute HornetQClient

        mvn exec:java

7. Check the server console for output from the service.  With the default
   configuration of the quickstart, you should see the output below in the
   AS server.log.
8. Undeploy the application

        mvn jboss-as:undeploy
        rm ${AS}/standalone/deployments/switchyard-quickstart-demo-policy-transaction-hornetq-jms.xml

```
:: WorkService :: Received command =>  rollback
:: WorkService :: Marked transaction to rollback!
:: WorkService :: Received command =>  rollback
:: WorkService :: Marked transaction to rollback!
:: WorkService :: Received command =>  rollback
:: WorkService :: Marked transaction to rollback!
:: WorkService :: Received command =>  rollback
:: WorkService :: Rollbacks completed
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

2. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Execute the test:

        mvn test -DskipTests=false
