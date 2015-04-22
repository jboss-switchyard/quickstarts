Introduction
============
This quickstart demonstrates the usage of the Camel Atom Component and it's binding feature, by polling for messages. 

Running the quickstart
======================
Please change connection parameters in src/main/resources/switchyard.xml to point your testing towards different Atom feeds.

- feedUri
- splitEntries
- filter
- feedHeader


Running the quickstart
======================


EAP
----------
1. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy

3. Watch the expected output print to the console.

4. Undeploy the quickstart:

        mvn clean -Pdeploy

Wildfly
----------
1. Start Wildfly in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy -Pwildfly

3. Watch the expected output print to the console.

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the camel-atom-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-atom-binding

4. Watch the expected output print to the console.

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-atom-binding



Expected Output
===============
The title and artist of the iTunes top 10 singles should print to the console.    There will be a delay between each entry that prints to the console.


## Further Reading

1. [Atom Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Atom)
