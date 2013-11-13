Introduction
============
This quickstart demonstrates the usage of the Camel Mail Component and its binding feature by receiving and sending messages. By default, a mock mail server is used for test purposes.

If you would like to test this binding please use a standalone mail server, for example Apache James, or create a test account.

Running the quickstart
======================
Please change the connection parameters in src/main/resources/META-INF/switchyard.xml to point to your testing mail accounts. For example if you use gmail :

- receiver PrintService (add secure="true" to camel:binding to use imaps instead of imap)
  - host = imap.gmail.com
  - username = your test account name (gmail.com username without the @gmail.com- change it from "switchyardtestaccount" to your gmail.com test username)
  - password = you test account password (change the INSERT-PASSWORD text to your test password)
  - consumer/folderName = inbox	

  <mail:binding.mail xmlns:mail="urn:switchyard-component-camel-mail:config:1.0" secure="true">
      <mail:host>imap.gmail.com</mail:host>
      <mail:username>switchyardtestaccount</mail:username>
      <mail:password>INSERT-PASSWORD</mail:password>
      <mail:consume accountType="imap">
          <mail:folderName>inbox</mail:folderName>
      </mail:consume>
  </mail:binding.mail>

- sender OutgoingPrintService (add secure="true" to camel:binding to use smtps instead of smtp)
  - host smtp.gmail.com
  - port
  - username = your test account name
  - password = your test account password
  - produce/from = your test account address 
  - producer/to = your real mail address

  <mail:binding.mail xmlns:mail="urn:switchyard-component-camel-mail:config:1.0" secure="true">
      <mail:host>smtp.gmail.com</mail:host>
      <mail:port>465</mail:port>
      <mail:username>switchyardtestaccount@gmail.com</mail:username>
      <mail:password>INSERT-PASSWORD</mail:password>
      <mail:produce>
          <mail:subject>Forwarded message</mail:subject>
          <mail:from>switchyardtestaccount@gmail.com</mail:from>
          <mail:to>INSERT-EMAIL-ADDRESS-TO</mail:to>
      </mail:produce>
  </mail:binding.mail>


You can then send plain text messages to test account, after processing you will receive copy with 'Greetings' as subject.

JBoss AS 7
----------
1. Build the quickstart:

        mvn -Dmaven.test.skip=true clean install

2. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Deploy the quickstart

        mvn -Dmaven.test.skip=true jboss-as:deploy

4. Wait a bit for mail poller to open mailbox and read messages.
5. Check the server console for output from the service. For every message you should see one entry with contents of the message. The best for testing is plaintext mail.


## Further Reading

1. [Mail Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Mail)
