Introduction
============
This quickstart demonstrates how to use the RemoteInvoker with services in SwitchYard using the SCA Binding.  The use
case for the example is evaluating an offer on an automobile and making a decision on whether to accept the offer or not.
There is a CreditCheck service implemented using business rules and a Dealer service which is implemented using CDI.  The
Dealer service evaluates the offer and submits the applicant to a credit check, before replying with an answer to the client.

The project includes unit tests to test each of the services individually and a test driver called RemoteClient which
demonstrates the use of an HTTP-based RemoteInvoker to invoke a SwitchYard service remotely.

![Remote Invoker Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/remote-invoker/remote-invoker.jpg)

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:

        mvn clean install

2. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

3. Deploy the Quickstart :

        mvn jboss-as:deploy

4. Use the RemoteClient class to send a request message to the Dealer service.  The client can be
   run from the command-line using:

        mvn exec:java

You should see the following in the command output:
```
    ==================================
    Was the offer accepted? true
    ==================================
```
## Further Reading

1. [Remote Invoker Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Remote+Invoker)
2. [SCA Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SCA)
