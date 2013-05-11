Introduction
============
This quickstart demonstrates the usage of the Camel SQL and it's binding feature, by retrieving messages from database and saving it to database. By default quickstart uses dummy datasource deployed under JBoss AS using non-persistent in memory database. It also uses a Quartz binding to generate test records. With typical production scenarios you don't need that.

Running the quickstart
======================
JBoss AS 7
----------
1. Build the quickstart:

        mvn clean install

2. Start JBoss AS 7 in standalone mode (other modes are fine too):

        ${AS}/bin/standalone.sh

3. Create database table using h2 console. To do that download [h2console](https://github.com/jboss-jdf/jboss-as-quickstart/blob/master/h2-console/h2console.war).
4. Open browser page http://localhost:8080/h2console
5. Put following data in connection parameters
    - connection url jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
    - username sa
    - password sa
6. Copy contents of schema.sql file and execute query.
7. Deploy the quickstart

        mvn jboss-as:deploy

8. Wait a bit for producer and consumer threads to start
9. Check the server console for output from the service

    Consumed Greeting [id: 2] from Foo to Bar
    Consumed Greeting [id: 3] from Tom to Rob

10. Execute following statement to add extra rows

        INSERT INTO greetings (sender,receiver) values ('John', 'Rambo');

11. After short time you shoild see in console message

    Consumed Greeting [id: x] from John to Rambo

## Further Reading

1. [Camel Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Bindings)
