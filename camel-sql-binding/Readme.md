Introduction
============
This quickstart demonstrates the usage of the Camel SQL and it's binding feature, by retrieving messages from database and saving it to database. By default quickstart uses dummy datasource deployed under JBoss AS using non-persistent in memory database. It also uses a Quartz binding to generate test records. With typical production scenarios you don't need that.

Running the quickstart
======================
JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode (other modes are fine too):

        ${AS}/bin/standalone.sh

2. Install the h2console into standalone/deployments. To do that download [h2console](https://github.com/jboss-jdf/jboss-as-quickstart/blob/master/h2-console/h2console.war).

3. Open browser page http://localhost:8080/h2console

4. Put following data in connection parameters
    - connection url jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
    - username sa
    - password sa

5. Copy contents of schema.sql file and execute query; this will create and populate the table.

6. Build and deploy the quickstart

        mvn install -Pdeploy

7. Wait a bit for producer and consumer threads to start

8. Check the server console for output from the service

    Consumed Greeting [id: 2] from Foo to Bar
    Consumed Greeting [id: 3] from Tom to Rob

9. Execute following statement in the h2console to add extra rows

        INSERT INTO greetings (sender,receiver) values ('John', 'Rambo');

10. After short time you shoild see in console message

    Consumed Greeting [id: x] from John to Rambo

11. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [SQL Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/SQL)
