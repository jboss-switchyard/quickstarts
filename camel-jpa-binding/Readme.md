Introduction
============
This quickstart demonstrates the usage of the Camel JPA and it's binding feature, by retrieving messages from database and saving it to database. By default quickstart uses dummy datasource deployed under JBoss AS using non-persistent in memory database.

Running the quickstart
======================
JBoss AS 7
----------
1. Build the quickstart:
    mvn clean install
2. Start JBoss AS 7 in standalone mode (other modes are fine too):
    ${AS}/bin/standalone.sh
3. Deploy the quickstart
    mvn jboss-as:deploy
4. Wait a bit for producer thread to start storing entities into database
5. Check the server console for output from the service

    Hey Tom please receive greetings from David sent at 16:23:15:032  
    Hey Magesh please receive greetings from Magesh sent at 16:23:15:034

6. You can publish additional greetings using h2 database console. To do that download [h2console](https://github.com/jboss-jdf/jboss-as-quickstart/blob/master/h2-console/h2console.war).
7. Open browser page http://localhost:8080/h2console
8. Put following data in connection parameters
    - connection url jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
    - username sa
    - password sa
9. Execute following statement
    INSERT INTO events (sender,receiver,createdAt) values ('John', 'Rambo', NOW());
9. After short time you shoild see in console message

    Hey Rambo please receive greetings from John sent at 17:20:01:542

## Further Reading

1. [Camel Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Bindings)

