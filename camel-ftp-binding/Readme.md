Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by binding 
to a ftp server. When file is created on ftp server service will be invoked.

Running the quickstart
======================

JBoss AS 7
----------
1. Ensure that connection parameters in src/main/resources/META-INF/switchyard.xml points to your server.
   The default parameters are:
```
    directory: .
    host: localhost
    port: 2222
    user: camel
    password: isMyFriend
```
2. Build the quickstart:

        mvn clean install -Dmaven.test.skip=true

    Tests should be skipped after parameter change because embedded server have hardcoded username and password.

3. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

4. Deploy the quickstart

        mvn jboss-as:deploy

5. Create file on ftp server using a standalone FTP client
6. Check the server console for output from the service.

## Further Reading

1. [FTP FTPS SFTP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/FTP+FTPS+SFTP)
