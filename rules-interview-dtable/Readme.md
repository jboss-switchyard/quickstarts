Introduction
============
This quickstart demonstrates the usage of a rules service which performs an age check.
The rules are specified using a DTABLE resource (and XLS resourceDetail) in the manifest.
The input is the incoming message content (an "Applicant").
The output is a boolean, and mapped using the implicit "globals" Map.

This quickstart also demonstrates how a domain property can be made available as a global variable inside your DRL.
See references to ${user.name} (system property) mapping to userName (domain property) mapping to userName (global variable) inside switchyard.xml.
Then look at Interview.drl and how the userName global variable is used in the DRL.

This quickstart can be executed using either a java interface or a web service interface.

![Rules Interview Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/rules-interview-dtable/rules-interview-dtable.jpg)


Preqrequisites 
==============
Maven

Running the quickstart
======================
1. Build the quickstart:

        mvn clean install

2. Run the test:

        (Using java interface:)
        mvn -Dtest=RulesInterviewTest test

        (Using web service interface:)
        mvn -Dtest=WebServiceTest test

## Further Reading

1. [Configuration Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Configuration)
2. [Rules Service Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Rules)
