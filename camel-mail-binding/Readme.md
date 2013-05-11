Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by receiving and sending messages. By default, for test purposes a mock mail server is used.
If you would like to test this binding please use a standalone mail server, for example Apache James, or create test account.

Running the quickstart
======================
Please change connection parameters in src/main/resources/switchyard.xml to point your testing mail accounts. For example if you use gmail

- receiver (add secure="true" to camel:binding to use imaps instead of imap)
  - host = imap.gmail.com
  - username = your test account name
  - password = you test account password
  - consumer/folderName = inbox	

- sender (add secure="true" to camel:binding to use smtps instead of smtp)
  - host smtp.gmail.com
  - username = your test account name
  - password = your test account password
  - produce/from = your test account name
  - producer/to = your real mail address

You can then send plain text messages to test account, after processing you will receive copy with 'Greetings' as subject.

JBoss AS 7
----------
1. Build the quickstart:

        mvn clean install

2. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Deploy the quickstart

        mvn jboss-as:deploy

4. Wait a bit for mail poller to open mailbox and read messages.
5. Check the server console for output from the service. For every message you should see one entry with contents of the message. The best for testing is plaintext mail.


## Further Reading

1. [Camel Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Camel+Bindings)
