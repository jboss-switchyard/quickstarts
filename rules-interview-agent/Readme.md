Introduction
============
This quickstart demonstrates the usage of a rules service which performs an age check.
The package resource is specified using annotations within the InterviewRules interface.       
The name of the service in the drl is specified using a Mapping annotation containing an MVEL expression.

This quickstart differs from rules-interview in a few ways:
1. A complied pkg is used instead of the original drl.
2. A Drools KnowledgeAgent is used, with the pkg resource being watched for changes.
3. After the test is run once, the pkg is updated with updated rules, and the test is run again.
   The once valid applicant being 20 is now invalid, given that the required age changed from 18 to 21. 

![Rules Interview Agent Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/rules-interview-agent/rules-interview-agent.jpg)

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
    mvn -Dtest=RulesInterviewTest test

Expected Output:
================
```
Running org.switchyard.quickstarts.rules.interview.RulesInterviewTest
INFO [drools.ResourceChangeService] Starting resource change service...
INFO  [drools.LogSystemEventListener] ResourceChangeNotification has started listening for ChangeSet publications
INFO  [drools.LogSystemEventListener] ResourceChangeScanner reconfigured with interval=1
INFO  [drools.LogSystemEventListener] ResourceChangeScanner reconfigured with interval=1
INFO  [drools.ResourceChangeService] Resource change service started.
INFO  [drools.LogSystemEventListener] ResourceChangeNotification scanner has started
INFO  [drools.LogSystemEventListener] KnowledgeAgent applying ChangeSet
INFO  [drools.LogSystemEventListener] KnowledgeAgent new KnowledgeBase now built and in use
********** Interview: Twenty is a valid applicant. **********
INFO  [drools.LogSystemEventListener] KnowledgeAgent applying ChangeSet
INFO  [drools.LogSystemEventListener] KnowledgeAgent new KnowledgeBase now built and in use
********** Interview: Twenty is NOT a valid applicant. **********
INFO  [drools.ResourceChangeService] Stopping resource change service...
INFO  [drools.ResourceChangeService] Resource change service stopped.
ResourceChangeNotification has stopped listening for ChangeSet publications
ResourceChangeNotification scanner has stopped
KnowledgeAgent unsubscribing from resource=[UrlResource path='file:/quickstarts/rules-interview-agent/target/classes/org/switchyard/quickstarts/rules/interview/Interview.pkg']
ResourceChangeNotification unsubscribing listener=org.drools.agent.impl.KnowledgeAgentImpl@10b41166 to resource=[UrlResource path='file:/quickstarts/rules-interview-agent/target/classes/org/switchyard/quickstarts/rules/interview/Interview.pkg']
ResourceChangeScanner unsubcribing notifier=org.drools.io.impl.ResourceChangeNotifierImpl@1de2481b to resource=[UrlResource path='file:/quickstarts/rules-interview-agent/target/classes/org/switchyard/quickstarts/rules/interview/Interview.pkg']
ResourceChangeScanner resource=[UrlResource path='file:/quickstarts/rules-interview-agent/target/classes/org/switchyard/quickstarts/rules/interview/Interview.pkg'] now has no subscribers
KnowledgeAgent has stopped listening for ChangeSet notifications
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 12.066 sec
```

## Further Reading

1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)

