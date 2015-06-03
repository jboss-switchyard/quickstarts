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

EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Use the RemoteClient class to send a request message to the Dealer service.  The client can be
   run from the command-line using:

        mvn exec:java

You should see the following in the command output:
```
    ==================================
    Was the offer accepted? true
    ==================================
```

4. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

mvn install -Pdeploy -Pwildfly

3. Use the RemoteClient class to send a request message to the Dealer service.  The client can be
run from the command-line using:

mvn exec:java

You should see the following in the command output:
```
==================================
Was the offer accepted? true
==================================
```

4. Undeploy the quickstart:

mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the remote-invoker quickstart :

karaf@root> features:install switchyard-quickstart-remote-invoker

4. To submit a request, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-remote-invoker


## Further Reading

1. [Remote Invoker Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Remote+Invoker)
2. [SCA Bindings Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SCA)
