Introduction
============
This quickstart demonstrates the usage of the Camel RSSComponent and its binding feature by polling for messages. 

Running the quickstart
======================
Please change connection parameters in src/main/resources/switchyard.xml to point your testing towards different RSS feeds.

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

3. Check the server console for output from the service.  

4. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone-full mode:

${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart

mvn install -Pdeploy -Pwildfly

3. Check the server console for output from the service.  

4. Undeploy the quickstart:

mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature :

karaf@root> features:install switchyard-quickstart-camel-rss-binding

4. Check the server console for output from the service.    

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-rss-binding


Expected Results
================
The titles and artists of the iTunes singles top 10 should print out to the console.   There will
be a delay between the printing of each entry.


## Further Reading

1. [RSS Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/RSS)
