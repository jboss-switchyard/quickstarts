Introduction
============
This quickstart is based on the Help Desk quickstart (please read: ../helpdesk/Readme.md), however differs in that the completion of human tasks gets done via a JSF web application.
The web application provides a form that allows two different people (krisv, a developer, and david, a user) to complete their work.
Please refer to the Running the quickstart section below for more information on using the web application.

Preqrequisites 
==============
Maven

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start the application server:
    cd switchyard-as7-0.4/bin ; ./standalone.sh
3. Deploy the web application:
    cp target/switchyard-quickstart-demo-helpdesk-webapp.war <path-to>/switchyard-as7-0.4/standalone/deployments/
4. Start the Human Task server:
    mvn exec:java -Dexec.args="start.taskserver"
5. In a web browser window, use the web application:
    Browse to http://localhost:8080/helpdesk/
    Select the User you want to act as.
    Note that you can toggle back and forth between users. (This would normally be automatically chosen based on the logged on user.)
    So far there are no processes started, so there are no tasks.
6. In a different console window, start a process (this will use the SOAP gateway):
    mvn exec:java -Dexec.args="start.process"
   You can do this as many times as you wish, starting as many processes as you wish.
7. Going back to your web browser window:
    As krisv (a developer), click the Submit button to get the list of tasks.
    As krisv, review the tasks you want to perform and click the Submit button again.
    As david (a user), click the Submit button to get the list of tasks. He will only have tasks if more details were required.
    If there were user tasks, check the tasks you want to complete and click the Submit button again.
    Continue toggling back and forth as the users until all tasks are completed.
    You can view the application server output in its console window to see the progression of the progress.
8. In a different console window, CLEANLY stop the Human Task server. (Do not use Ctrl-C in the previous console window!):
    mvn exec:java -Dexec.args="stop.taskserver"
   or
    rm target/taskserver.rm2stop

Expected TaskServer Output:
===========================
INFO  [org.switchyard.component.bpm.task.service.jbpm.JBPMTaskServer] Starting jBPM TaskServer on 127.0.0.1:9123...
INFO  [org.switchyard.component.bpm.task.service.jbpm.JBPMTaskServer] jBPM TaskServer started on 127.0.0.1:9123.
INFO  [org.switchyard.quickstarts.demos.helpdesk.HelpDeskTests] ********** IMPORTANT: To CLEANLY stop the TaskServer, in another window either run mvn exec:java -Dexec.args="stop.taskserver" or simply delete the .../switchyard-quickstarts/demos/helpdesk-webapp/target/taskserver.rm2stop file. Do not use Ctrl-C!  **********
INFO  [org.switchyard.component.bpm.task.service.jbpm.JBPMTaskServer] Stopping jBPM TaskServer on 127.0.0.1:9123...
INFO  [org.switchyard.component.bpm.task.service.jbpm.JBPMTaskServer] jBPM TaskServer on 127.0.0.1:9123 stopped.

Expected Application Server Output:
===================================
(Note: Your outcome might be different from below based on the result of the ticket review.)
INFO  [org.switchyard.quickstarts.demos.helpdesk.TicketManagementServiceBean] (pool-11-thread-2) ********** opening ticket **********
INFO  [stdout] (NioProcessor-20) ********** after (random for test purpose) review, ticket status set to approved **********
INFO  [org.switchyard.quickstarts.demos.helpdesk.TicketManagementServiceBean] (NioProcessor-20) ********** approving ticket **********
INFO  [org.switchyard.quickstarts.demos.helpdesk.TicketManagementServiceBean] (NioProcessor-20) ********** closing ticket **********

## Further Reading

1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)
2. [SOAP Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP+Bindings)
3. [BPM Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/BPM+Services)
4. [Bean Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Bean+Services)

