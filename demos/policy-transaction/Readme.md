Introduction
============
This quickstart demonstrates how policy can be used to control the transactional characteristics of a service invocation.  The only service in the application is a Bean service called "WorkService".  The service accepts commands and can be instructed to rollback by sending a message containing the string "rollback".

Running the quickstart
======================

1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone mode:
    ./standalone --server-config=standalone-preview.xml
3. Create the queues for the JMS gateway bindings
    ./jboss-admin.sh file=src/test/resources/create-resources.cli
5. Deploy the quickstart
    cp target/switchyard-quickstarts-camel-jms-binding-{version}.jar ${AS7}/standalone/deployments
6. Execute HornetQClient
    mvn exec:java
7. Check the server console for output from the service.  With the default
   configuration of the quickstart, you should see the following in the 
   server log:

`:: WorkService :: Received command =>  rollback  
:: WorkService :: Marked transaction to rollback!  
:: WorkService :: Received command =>  rollback  
:: WorkService :: Marked transaction to rollback!  
:: WorkService :: Received command =>  rollback  
:: WorkService :: Marked transaction to rollback!  
:: WorkService :: Received command =>  rollback  
:: WorkService :: Rollbacks completed`    
   

Scenarios
=========
You can test any of these transaction policy scenarios using this quickstart:

1) Global transaction propagated to the bean service.  This is the default 
   configuration of the quickstart and requires the WorkServiceBean to be
   annotated with @ManagedTransaction(ManagedTransactionType.SHARED) and 
   messages to be sent to the policyQSTransacted queue.
   
2) Policy violation - transaction required to be propagated, but no transaction
   provided by the gateway.  To exercise this scenario, send a message to the 
   policyQSNonTransacted queue with ManagedTransactionType.SHARED on the bean
   service.
   
3) Suspend incoming transaction.  Change the WorkServiceBean transaction 
   annotation type to ManagedTransactionType.LOCAL and send a message to the
   policyQSTransacted queue with the command "rollback".  Check the output and
   note that the rollback does not impact the transaction used to receive the
   JMS message.
   
   
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

o Deploying this quickstart on AS 7.0.2 appears to lead to shutdown delays due
  to a RM issue between HornetQ and the AS transaction manager.  The root cause
  of this is under investigation, but be advised that a server shutdown may take
  > 2 minutes when this issue occurs.
  

