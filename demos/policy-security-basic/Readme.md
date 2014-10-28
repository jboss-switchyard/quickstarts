Introduction
============
This quickstart demonstrates how policy can be used to control the security characteristics of a
service invocation.  The only service in the application is a Bean service called "WorkService".
SSL is used for "confidentiality", and Basic Authentication is used for "clientAuthentication".


Running the quickstart
======================

JBoss AS 7
----------

1. Create an application user:

        ${AS}/bin/add-user.sh --user kermit --password the-frog-1 --realm ApplicationRealm --group friend

2. Start JBoss AS in standalone mode:

        ${AS}/bin/standalone.sh

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Execute the test. (See "Options" section below.)

5. Check the server console for output from the service.

6. Undeploy the application

        mvn clean -Pdeploy

Karaf
-----
Instead of steps 1-3,6 above for JBoss AS 7...

1. Create a ${KARAF}/quickstarts/demos/policy-security-basic/ directory, and copy connector.jks into it.

2. Create a ${KARAF}/etc/org.ops4j.pax.web.cfg file, with the following contents:

org.osgi.service.http.enabled=true
org.osgi.service.http.port=8181
org.osgi.service.http.secure.enabled=true
org.osgi.service.http.port.secure=8183
org.ops4j.pax.web.ssl.keystore=quickstarts/demos/policy-security-basic/connector.jks
org.ops4j.pax.web.ssl.keystore.type=JKS
org.ops4j.pax.web.ssl.password=changeit
org.ops4j.pax.web.ssl.keypassword=changeit
org.ops4j.pax.web.ssl.clientauthwanted=false
org.ops4j.pax.web.ssl.clientauthneeded=false

3. Add this line to ${KARAF}/etc/users.properties:

       kermit=the-frog-1,friend

4. When executing the test (as directed below), add the following system property: -Dorg.switchyard.component.soap.client.port=8183

Options
=======

When running with no options:

    mvn exec:java

, you will be hitting the http (non-SSL) URL, and see this in your log:

    Caused by: org.switchyard.exception.SwitchYardException: Required policies have not been provided: authorization clientAuthentication confidentiality

When running with this option:

    mvn exec:java -Dexec.args="confidentiality clientAuthentication" -Djavax.net.ssl.trustStore=connector.jks

, you will be hitting the https (SSL) URL and providing authentication information, and see this in your log:

    :: WorkService :: Received work command => CMD-1398262304944 (caller principal=kermit, in roles? 'friend'=true 'enemy'=false)

    (Because the WorkService is secured, you will see the not-null principal, and true for the expected security role.)

You can play with the exec.args and only specify one of "confidentiality" or "clientAuthentication". I bet you can guess what will happen... ;)
