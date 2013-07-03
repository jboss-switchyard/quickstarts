Introduction
============
This quickstart demonstrates the usage of a BPM service in conjuction with a SOAP gateway.
A BPM process (HelpDeskService.bpmn) is deployed as the service, and a SOAP gateway is deployed using the specified WSDL (HelpDeskService.wsdl).
First, a ticket is opened and a developer has to review it.
Completion of human tasks gets done via a JSF web application, which provides a form that allows two different people (krisv, a developer, and david, a user) to complete their work.
If the ticket is approved, the developer has to resolve the ticket, then it is closed.
If the ticket is rejected, no further action is necessary.
If more details are requested, a user must provide more details. Then, the process loops back to the review stage again.
Finally, the process reaches its end.

If you would like to have BAM (Business Activity Monitoring) events from the process execution get stored in the database, uncomment these lines in
src/main/java/org/switchyard/quickstarts/demos/helpdesk/HelpDeskServiceProcess.java:
```
//import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
//import org.switchyard.component.common.knowledge.annotation.Listener;
//listeners={@Listener(JPAWorkingMemoryDbLogger.class)},
```

![Helpdesk Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/demos/helpdesk/helpdesk.jpg)


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
    ${AS}/bin/standalone.sh

3. Deploy the web application:
   mvn jboss-as:deploy

4. In a web browser window, use the web application:
    Browse to http://localhost:8080/helpdesk/
    Select the User you want to act as.
    Note that you can toggle back and forth between users. (This would normally be automatically chosen based on the logged on user.)
    So far there are no processes started, so there are no tasks.

5. In a different console window, start a process (this will use the SOAP gateway):
    mvn exec:java
    You can do this as many times as you wish, starting as many processes as you wish.

6. Going back to your web browser window:
    As krisv (a developer), click the Submit button to get the list of tasks.
    As krisv, review the tasks you want to perform and click the Submit button again.
    As david (a user), click the Submit button to get the list of tasks. He will only have tasks if more details were required.
    If there were user tasks, check the tasks you want to complete and click the Submit button again.
    Continue toggling back and forth as the users until all tasks are completed.
    You can view the application server output in its console window to see the progression of the progress.

Expected Output:
================
(Note: Your outcome might be different from below based on the result of the ticket review.)
```
INFO  [org.switchyard.quickstarts.demos.helpdesk.TicketManagementServiceBean] (http-/127.0.0.1:8080-2) ********** opening ticket **********
INFO  [org.switchyard.quickstarts.demos.helpdesk.TicketManagementServiceBean] (http-/127.0.0.1:8080-1) ********** requesting details **********
INFO  [org.switchyard.quickstarts.demos.helpdesk.TicketManagementServiceBean] (http-/127.0.0.1:8080-1) ********** approving ticket **********
INFO  [org.switchyard.quickstarts.demos.helpdesk.TicketManagementServiceBean] (http-/127.0.0.1:8080-1) ********** closing ticket **********
```

## Further Reading

1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)
2. [SOAP Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP)
3. [BPM Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/BPM)
4. [Bean Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Bean)
