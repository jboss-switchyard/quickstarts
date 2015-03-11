Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by binding 
to a JMS Queue. When a message arrives in this queue the service will be invoked.

![Camel JMS Binding Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/camel-jms-binding/camel-jms-binding.jpg)


Running the quickstart
======================


EAP
----------
1. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Create an application user:

        ${AS}/bin/add-user.sh --user guest --password guestp.1 --realm ApplicationRealm --group guest

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Execute HornetQClient

        mvn exec:java

5. Check the server console for output from the service.

6. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Create an application user:

        ${AS}/bin/add-user.sh --user guest --password guestp.1 --realm ApplicationRealm --group guest

3. Build and deploy the quickstart

        mvn install -Pdeploy -Pwildfly

4. Execute HornetQClient

        mvn exec:java -Pwildfly

5. Check the server console for output from the service.

6. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

        ${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the camel-jms-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-jms-binding

4. To submit a jms request, run the quickstart client :
<br/>
```
        mvn exec:java -Pkaraf
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-jms-binding



## Further Reading

1. [JMS Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JMS)
