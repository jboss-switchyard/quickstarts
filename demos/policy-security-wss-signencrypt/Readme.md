Introduction
============
This quickstart demonstrates how policy can be used to control the security characteristics of a
service invocation.  The only service in the application is a Bean service called "WorkService".
SSL is used for "confidentiality", and WS-Security is used to verify the Signature and Encryption
of the SOAP message.


Running the quickstart
======================

1. Build the quickstart:

        mvn clean install

2. Create a keystore to support SSL:

        cd ${AS}/standalone/configuration
        keytool -genkey -alias tomcat -keyalg RSA -keypass changeit -keystore tomcat.jks

    (password is "changeit")
3. Add the required https connector to the web subsystem in standalone.xml to support SSL. (include contents of connector.xml)
4. Deploy the quickstart

        mvn jboss-as:deploy

5. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

6. Execute the test
    See "Options" section below.
7. Check the server console for output from the service.
8. Undeploy the application

        mvn jboss-as:undeploy

Options
=======

When running with no options:

    mvn exec:java

, you will be hitting the http (non-SSL) URL without providing authentication information, and see this in your log:

```
[org.apache.cxf.phase.PhaseInterceptorChain] (http-/127.0.0.1:8080-1) Interceptor for
{urn:switchyard-quickstart-demo:policy-security-wss-signencrypt:0.1.0}WorkService#{urn:switchyard-quickstart-demo:policy-security-wss-signencrypt:0.1.0}doWork has thrown
exception, unwinding now: org.apache.cxf.ws.policy.PolicyException: These policy alternatives can not be satisfied: 
{http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702}X509Token: The received token does not match the token inclusion requirement
{http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702}SignedParts: {http://schemas.xmlsoap.org/soap/envelope/}Body not SIGNED
{http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702}EncryptedParts: {http://schemas.xmlsoap.org/soap/envelope/}Body not ENCRYPTED
```

When running with this option:

    mvn exec:java -Dexec.args="signencrypt"

, you will be hitting the http (non-SSL) URL while providing authentication information, and see this in your log:

    Caused by: org.switchyard.exception.SwitchYardException: Required policies have not been provided: confidentiality

When running with this option:

    mvn exec:java -Dexec.args="confidentiality signencrypt" -Djavax.net.ssl.trustStore=[path to tomcat.jks created in step 2]

, you will be hitting the https (SSL) URL while providing authentication information, and see this in your log:

    INFO  [org.switchyard.quickstarts.demo.policy.security.wss.signencrypt.WorkServiceBean] (http--127.0.0.1-8443-1) :: WorkService :: Received work command => CMD-86

Success!
