Introduction
============
This quickstart demonstrates the usage of a BPM service in conjuction with a SOAP gateway.
A BPM process (HelpDeskService.bpmn) is deployed as the service, and a SOAP gateway is deployed using the specified WSDL (HelpDeskService.wsdl).
First, a ticket is opened and a developer has to review it.
The code then randomly decides whether or not the ticket is approved, rejected or more details should be requested.  (This is to simulate a human evaluation.)
If the ticket is approved, the developer has to resolve the ticket, then it is closed.
If the ticket is rejected, no further action is necessary.
If more details are requested, a user must provide more details. Then, the process loops back to the review stage again.
Finally, the process reaches its end.

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
2. Run the test:
    mvn -Dtest=HelpDeskTests test

Expected Output:
================
(Note: Remember that the review has a random outcome, thus your output might differ from below.)
```
Running org.switchyard.quickstarts.demos.helpdesk.HelpDeskTests
INFO  [org.switchyard.component.bpm.task.impl.TaskServerImpl] Starting jBPM TaskServer on 127.0.0.1:9123...
INFO  [org.switchyard.component.bpm.task.impl.TaskServerImpl] jBPM TaskServer started on 127.0.0.1:9123.
INFO  [org.switchyard.component.soap.InboundHandler] Publishing WebService at http://127.0.0.1:18001/HelpDeskService
INFO  [org.switchyard.quickstarts.demos.helpdesk.TicketManagementServiceBean] ********** opening ticket **********
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Connecting jBPM TaskClient to 127.0.0.1:9123...
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] jBPM TaskClient connected to 127.0.0.1:9123.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Found 0 tasks for david.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Found 1 task for krisv.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 1 claimed by krisv.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 1 started by krisv.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 1 completed by krisv.
********** after (random for test purpose) review, ticket status set to requested **********
INFO  [org.switchyard.quickstarts.demos.helpdesk.TicketManagementServiceBean] ********** requesting details **********
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Found 1 task for david.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 2 claimed by david.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 2 started by david.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 2 completed by david.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Found 0 tasks for krisv.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Found 0 tasks for david.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Found 1 task for krisv.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 3 claimed by krisv.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 3 started by krisv.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 3 completed by krisv.
********** after (random for test purpose) review, ticket status set to approved **********
INFO  [org.switchyard.quickstarts.demos.helpdesk.TicketManagementServiceBean] ********** approving ticket **********
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Found 0 tasks for david.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Found 1 task for krisv.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 4 claimed by krisv.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 4 started by krisv.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Task 4 completed by krisv.
INFO  [org.switchyard.quickstarts.demos.helpdesk.TicketManagementServiceBean] ********** closing ticket **********
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Found 0 tasks for david.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Found 0 tasks for krisv.
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] Disconnecting jBPM TaskClient from 127.0.0.1:9123...
DEBUG [org.switchyard.component.bpm.task.impl.TaskClientImpl] jBPM TaskClient disconnected from 127.0.0.1:9123.
INFO  [org.switchyard.component.soap.InboundHandler] WebService {urn:switchyard-quickstart-demo:helpdesk:1.0}HelpDeskService:HelpDeskServicePort stopped.
INFO  [org.switchyard.component.bpm.task.impl.TaskServerImpl] Stopping jBPM TaskServer on 127.0.0.1:9123...
INFO  [org.switchyard.component.bpm.task.impl.TaskServerImpl] jBPM TaskServer on 127.0.0.1:9123 stopped.
```

## Further Reading

1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)
2. [SOAP Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SOAP+Bindings)
3. [BPM Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/BPM+Services)
4. [Bean Services Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Bean+Services)

