Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by binding 
to a ftp server. When file is created on ftp server service will be invoked.

Running the quickstart
======================


EAP
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
2. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Build and deploy the quickstart

        mvn install -Pdeploy -DskipTests=true

    Tests should be skipped after parameter change because embedded server have hardcoded username and password.

4. Create a file on the ftp server using a standalone FTP client.

5. Check the server console for output from the service.

6. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
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
2. Start Wildfly in standalone-full mode:

${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Build and deploy the quickstart

mvn install -Pdeploy -Pwildfly -DskipTests=true

Tests should be skipped after parameter change because embedded server have hardcoded username and password.

4. Create file on ftp server using a standalone FTP client

5. Check the server console for output from the service.

6. Undeploy the quickstart:

mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the camel-ftp-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-ftp-binding

4. Create a file on the ftp server using a standalone FTP client.

5. Check the server console for output from the service.

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-ftp-binding


## Further Reading

1. [FTP FTPS SFTP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/FTP+FTPS+SFTP)
