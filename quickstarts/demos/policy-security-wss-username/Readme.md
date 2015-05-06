Introduction
============
This quickstart demonstrates how policy can be used to control the security characteristics of a
service invocation.  The only service in the application is a Bean service called "WorkService".
SSL is used for "confidentiality", and WS-Security UsernameToken is used for "clientAuthentication".


Running the quickstart
======================


EAP
----------

1. Create an application user:

        ${AS}/bin/add-user.sh -a --user kermit --password the-frog-1 --group friend

2. Start JBoss EAP in standalone mode:

        ${AS}/bin/standalone.sh

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Execute the test. (See "Options" section below.)

5. Check the server console for output from the service.

6. Undeploy the application

        mvn clean -Pdeploy


Wildfly
----------


1. Create an application user:

        ${AS}/bin/add-user.sh -a --user kermit --password the-frog-1 --group friend

2. Start Wildfly in standalone mode :

        ${AS}/bin/standalone.sh

3. Build and deploy the demo :

        mvn install -Pdeploy -Pwildfly

4. Execute the test. (See "Options" section below.)

5. Check the server console for output from the service.

6. Undeploy the application

        mvn clean -Pdeploy -Pwildfly

Warning --> Wildfly 8.0.0 When the application is undeployed, it is required to restart the server to get all the undeployment changes done.

Options
=======

When running with no options:

    mvn exec:java

You will be hitting the http (non-SSL) URL without providing authentication information, and see this in your log:

    Caused by: org.apache.ws.security.WSSecurityException: Failed Authentication : Subject has not been created

When running with this option:

    mvn exec:java -Dexec.args="clientAuthentication"

You will be hitting the http (non-SSL) URL while providing authentication information, and see this in your log:

    Caused by: org.switchyard.exception.SwitchYardException: Required policies have not been provided: confidentiality

When running with this option:

    mvn exec:java -Dexec.args="confidentiality clientAuthentication" -Djavax.net.ssl.trustStore=connector.jks

You will be hitting the https (SSL) URL while providing authentication information, and see this in your log:

    :: WorkService :: Received work command => CMD-1398262803294 (caller principal=kermit, in roles? 'friend'=true 'enemy'=false)

    (Because the WorkService is secured, you will see the not-null principal, and true for the expected security role.)

Success!
