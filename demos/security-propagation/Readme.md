Introduction
============
This quickstart builds on the policy-security-basic quickstart. There is still a Bean service
called "WorkService" which is protected by security policy (SSL is used for "confidentiality",
and Basic Authentication is used for "clientAuthentication"). However this quickstart differs
in that it calls another Bean service called "BackEndService" which is also protected by
security policy, but there is no need for re-authentication, since the current security
context is propagated from one service to the next. Furthermore, an EJB which itself is
protected via annotations is also called, and the security context is propagated there too.


Running the quickstart
======================

1. Create an application user:

        ${AS}/bin/add-user.sh

        (Add username "kermit", password "the-frog-1", and role "friend".)

2. Start JBoss AS in standalone mode:

        ${AS}/bin/standalone.sh

3. Build and deploy the ejb, then the basic application

        cd ejb ; mvn install -Pdeploy
        cd ../basic ; mvn install -Pdeploy

4. Execute the test. (See "Options" section below.)

5. Check the server console for output from the service.

6. Undeploy the application, then the ejb

        mvn clean -Pdeploy
		cd ../ejb ; mvn clean -Pdeploy


Options
=======

When running with no options:

    mvn exec:java

, you will be hitting the http (non-SSL) URL, and see this in your log:

    Caused by: org.switchyard.exception.SwitchYardException: Required policies have not been provided: authorization clientAuthentication confidentiality

When running with this option:

    mvn exec:java -Dexec.args="confidentiality clientAuthentication" -Djavax.net.ssl.trustStore=connector.jks

, you will be hitting the https (SSL) URL and providing authentication information, and see this in your log:

    :: WorkService :: Received work command => CMD-1401308184129 (caller principal=kermit, in roles? 'friend'=true 'enemy'=false)
    :: BackEndService :: process => CMD-1401308184129 (caller principal=kermit, in roles? 'friend'=true 'enemy'=false)
    :: WorkService :: returned from BackEndService => Processed by BackEndService: CMD-1401308184129
    :: TestEJBBean :: process => CMD-1401308184129 (caller principal=[roles=[friend],principal=kermit], in roles? 'friend'=true 'enemy'=false)
    :: WorkService :: returned from TestEJBBean => Processed by TestEJBBean: CMD-1401308184129

    (Because the WorkService, BackEndService and TestEBJBean are secured, you will see the not-null principal, and true for the expected security role.)

You can play with the exec.args and only specify one of "confidentiality" or "clientAuthentication". I bet you can guess what will happen... ;)
