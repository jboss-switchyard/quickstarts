Introduction
============
This quickstart demonstrates the usage of a rules service which performs an age check.  
The drl resource is specified using annotations within the InterviewRules interface.       

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
********** David is a valid applicant. **********  
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.131 sec`  
```

## Further Reading

1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)

