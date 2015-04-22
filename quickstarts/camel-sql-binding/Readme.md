Introduction
============
This quickstart demonstrates the usage of the Camel SQL and it's binding feature, by retrieving messages from database and saving it to database. By default quickstart uses dummy datasource deployed under JBoss AS using non-persistent in memory database. It also uses a Quartz binding to generate test records. With typical production scenarios you don't need that.

Running the quickstart
======================


EAP
----------
1. Start EAP in standalone mode (other modes are fine too):

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart

        mvn install -Pdeploy,db

3. Wait a bit for producer and consumer threads to start

4. Check the server console for output from the service

    Consumed Greeting [id: 2] from Foo to Bar
    Consumed Greeting [id: 3] from Tom to Rob

5. Undeploy the quickstart:

        mvn clean -Pdeploy,db


Wildfly
----------
1. Start Wildfly in standalone mode (other modes are fine too):

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart

        mvn install -Pdeploy,db

3. Wait a bit for producer and consumer threads to start

4. Check the server console for output from the service

    Consumed Greeting [id: 2] from Foo to Bar
    Consumed Greeting [id: 3] from Tom to Rob

5. Undeploy the quickstart:

        mvn clean -Pdeploy,db


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Build the Quickstart and setup the database

    mvn install -Pdb

3. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

4. Install the feature for the camel-sql-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-sql-binding

5. Check the server console for output from the service

Consumed Greeting [id: 2] from Foo to Bar
Consumed Greeting [id: 3] from Tom to Rob

6. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-sql-binding

7. Shutdown the database

    mvn clean -Pdb


## Further Reading

1. [SQL Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SQL)
