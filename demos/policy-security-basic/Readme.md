Introduction
============
This quickstart demonstrates how policy can be used to control the security characteristics of a
service invocation.  The only service in the application is a Bean service called "WorkService".
SSL is used for "confidentiality", and Basic Authentication is used for "clientAuthentication".


Running the quickstart
======================

1. Build the quickstart:

        mvn clean install

2. Create a keystore to support SSL:

        cd ${AS}/standalone/configuration
        keytool -genkey -alias tomcat -keyalg RSA -keypass changeit -keystore tomcat.jks

    (password is "changeit")
3. Add the required https connector to the web subsystem in standalone.xml to support SSL. (include contents of connector.xml)
4. Create an application user:

	    ${AS}/bin/add-user.sh

    Add username "kermit", password "the-frog-1", and role "friend".
5. Deploy the quickstart

        mvn jboss-as:deploy

6. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

7. Execute the test
    See "Options" section below.
8. Check the server console for output from the service.
9. Undeploy the application

        mvn jboss-as:undeploy

Options
=======

When running with no options:

    mvn exec:java

, you will be hitting the http (non-SSL) URL, and see this in your log:

    Caused by: org.switchyard.exception.SwitchYardException: Required policies have not been provided: authorization clientAuthentication confidentiality

When running with this option:

    mvn exec:java -Dexec.args="confidentiality clientAuthentication"

, you will be hitting the https (SSL) URL and providing authentication information, and see this in your log:

    INFO  [org.switchyard.quickstarts.demo.policy.security.basic.WorkServiceBean] (http--127.0.0.1-8443-1) :: WorkService :: Received work command => CMD-1345738943385

You can play with the exec.args and only specify one of "confidentiality" or "clientAuthentication". I bet you can guess what will happen... ;)
