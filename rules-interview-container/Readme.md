Introduction
============
This quickstart demonstrates the usage of a rules service which performs an age check.  
The drl resource is specified using annotations within the InterviewRules interface.       
The name of the service in the drl is specified using a Mapping annotation containing an MVEL expression.

This rules-interview-container quickstart differs from the rules-interview quickstart.
A /META-INF/kmodule.xml is used, which is referenced by both the <container> element in switchyard.xml, and the KIE/Drools container.
The rules-interview quickstart, on the other hand, manually lists the required resource.

![Rules Interview Container Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/rules-interview-container/rules-interview-container.jpg)


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
********** Interview: Twenty is a valid applicant. **********  
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.131 sec`  
```

## Further Reading

1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)

