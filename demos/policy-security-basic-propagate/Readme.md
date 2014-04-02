Introduction
============
This quickstart demonstrates how policy can be used to control the security characteristics of a
service invocation.  There are two services in the application. There is a Bean service called
"WorkService" which calls another Bean service called "BackEndService".  This quickstart differs
from policy-security-basic in that the security context is propagated from the WorkService to the
BackEndService, without need for re-authentication.
SSL is used for "confidentiality", and Basic Authentication is used for "clientAuthentication".


Running the quickstart
======================

1. Create an application user:

        ${AS}/bin/add-user.sh

        (Add username "kermit", password "the-frog-1", and role "friend".)

2. Start JBoss AS in standalone mode:

        ${AS}/bin/standalone.sh

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Execute the test. (See "Options" section below.)

5. Check the server console for output from the service.

6. Undeploy the application

        mvn clean -Pdeploy


Options
=======

When running with no options:

    mvn exec:java

, you will be hitting the http (non-SSL) URL, and see this in your log:

    Caused by: org.switchyard.exception.SwitchYardException: Required policies have not been provided: authorization clientAuthentication confidentiality

When running with this option:

    mvn exec:java -Dexec.args="confidentiality clientAuthentication" -Djavax.net.ssl.trustStore=connector.jks

, you will be hitting the https (SSL) URL and providing authentication information, and see this in your log:

    :: WorkService :: Received work command => CMD-1398262425267 (caller principal=kermit, in roles? 'friend'=true 'enemy'=false)
    :: BackEndService :: process => CMD-1398262425267 (caller principal=kermit, in roles? 'friend'=true 'enemy'=false)
    :: WorkService :: BackEndService received => Processed by BackEndService: CMD-1398262425267

    (Because the WorkService and BackEndService are secured, you will see the not-null principal, and true for the expected security role.)

You can play with the exec.args and only specify one of "confidentiality" or "clientAuthentication". I bet you can guess what will happen... ;)
