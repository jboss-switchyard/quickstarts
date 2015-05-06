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

EAP
----------

1. Create an application user:

        ${AS}/bin/add-user.sh -a --user kermit --password the-frog-1 --group friend

2. Start JBoss AS in standalone mode:

        ${AS}/bin/standalone.sh

3. Build and deploy the ejb, then the basic application

        cd ejb; mvn install -Pdeploy
        cd ../basic; mvn install -Pdeploy

4. Execute the test. (See "Options" section below.)

5. Check the server console for output from the service.

6. Undeploy the application, then the ejb

        mvn clean -Pdeploy
        cd ../ejb; mvn clean -Pdeploy


Wildfly
----------


1. Create an application user:

        ${AS}/bin/add-user.sh -a --user kermit --password the-frog-1 --group friend

2. Edit the standalone.xml placed on ${WILDFLY_HOME}/standalone/configuration

      Remove the default-security-domain tag inside of the ejb3 domain

        <subsystem xmlns="urn:jboss:domain:ejb3:2.0">
            <session-bean>
                <stateful default-access-timeout="5000" cache-ref="simple" passivation-disabled-cache-ref="simple"/>
                <singleton default-access-timeout="5000"/>
            </session-bean>
            ... 
-           <default-security-domain value="other"/>
            <default-missing-method-permissions-deny-access value="true"/>
        </subsystem>


3. Start Wildfly in standalone mode :
    
        ${AS}/bin/standalone.sh

4. Build and deploy the demo : 

       cd ejb; mvn install -Pdeploy -Pwildfly
       cd ../basic; mvn install -Pdeploy -Pwildfly

5. Execute the test. (See "Options" section below.)

6. Check the server console for output from the service.

7. Undeploy the application, then the ejb

        mvn clean -Pdeploy -Pwildfly
        cd ../ejb; mvn clean -Pdeploy -Pwildfly

Warning --> Wildfly 8.0.0 When the application is undeployed, it is required to restart the server to get all the undeployment changes done. 



Options
=======

When running with no options:

    mvn exec:java

You will be hitting the http (non-SSL) URL, and see this in your log:

    Caused by: org.switchyard.exception.SwitchYardException: Required policies have not been provided: authorization clientAuthentication confidentiality

When running with this option:

    mvn exec:java -Dexec.args="confidentiality clientAuthentication" -Djavax.net.ssl.trustStore=connector.jks

You will be hitting the https (SSL) URL and providing authentication information, and see this in your log:

    :: WorkService :: Received work command => CMD-1401308184129 (caller principal=kermit, in roles? 'friend'=true 'enemy'=false)
    :: BackEndService :: process => CMD-1401308184129 (caller principal=kermit, in roles? 'friend'=true 'enemy'=false)
    :: WorkService :: returned from BackEndService => Processed by BackEndService: CMD-1401308184129
    :: TestEJBBean :: process => CMD-1401308184129 (caller principal=[roles=[friend],principal=kermit], in roles? 'friend'=true 'enemy'=false)
    :: WorkService :: returned from TestEJBBean => Processed by TestEJBBean: CMD-1401308184129

    (Because the WorkService, BackEndService and TestEBJBean are secured, you will see the not-null principal, and true for the expected security role.)

You can play with the exec.args and only specify one of "confidentiality" or "clientAuthentication". I bet you can guess what will happen... ;)
