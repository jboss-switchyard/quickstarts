Introduction
============
This quickstart demonstrates the usage of the Camel SQL and it's binding feature, by retrieving messages from database and saving it to database. By default quickstart uses dummy datasource deployed under JBoss AS using non-persistent in memory database. It also uses a Quartz binding to generate test records. With typical production scenarios you don't need that.

Running the quickstart
======================
JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode (other modes are fine too):

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart

        mvn install -Pdeploy,db

3. Wait a bit for producer and consumer threads to start

4. Check the server console for output from the service

    Consumed Greeting [id: 2] from Foo to Bar
    Consumed Greeting [id: 3] from Tom to Rob

5. Undeploy the quickstart:

        mvn clean -Pdeploy,db

## Further Reading

1. [SQL Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SQL)
