Introduction
============
This quickstart demonstrates the usage of the Camel JPA and it's binding feature, by retrieving messages from database and saving it to database. By default quickstart uses dummy datasource deployed under JBoss AS using non-persistent in memory database.

Running the quickstart
======================


EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart : 

        mvn install -Pdeploy

3. Wait a bit for producer thread to start storing entities into database

4. Check the server console for output from the service

    Hey Tom please receive greetings from David sent at 16:23:15:032  
    Hey Magesh please receive greetings from Magesh sent at 16:23:15:034

5. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart : 

        mvn install -Pdeploy -Pwildfly

3. Wait a bit for producer thread to start storing entities into database

4. Check the server console for output from the service

Hey Tom please receive greetings from David sent at 16:23:15:032  
Hey Magesh please receive greetings from Magesh sent at 16:23:15:034

5. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

        ${KARAF_HOME}/bin/karaf

2. Build the Quickstart for karaf :

        mvn install -Pkaraf

3. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

4. Install the feature for the camel-jpa-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-jpa-binding

5. Check the console log for output from the service

Hey Tom please receive greetings from David sent at 16:23:15:032
Hey Magesh please receive greetings from Magesh sent at 16:23:15:034

6. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-jpa-binding



## Further Reading

1. [JPA Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/JPA)
