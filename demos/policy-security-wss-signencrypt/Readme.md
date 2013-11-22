Introduction
============
This quickstart demonstrates how policy can be used to control the security characteristics of a
service invocation.  The only service in the application is a Bean service called "WorkService".
SSL and/or WS-Security encryption can be used for "confidentiality", and WS-Security is used to verify the Signature and Encryption
of the SOAP message.


Running the quickstart
======================

1. Start JBoss AS in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart

        mvn install -Pdeploy

3. Execute the test. (See "Options" section below.)

4. Check the server console for output from the service.

5. Undeploy the application

        mvn clean -Pdeploy


Options
=======

When running with no options:

    mvn exec:java

, you will be hitting the http (non-SSL) URL while NOT providing signed and encrypted information, and see this in your log:

```
[org.apache.cxf.phase.PhaseInterceptorChain] (http-/127.0.0.1:8080-1) Interceptor for
{urn:switchyard-quickstart-demo:policy-security-wss-signencrypt:0.1.0}WorkService#{urn:switchyard-quickstart-demo:policy-security-wss-signencrypt:0.1.0}doWork has thrown
exception, unwinding now: org.apache.cxf.ws.policy.PolicyException: These policy alternatives can not be satisfied: 
{http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702}X509Token: The received token does not match the token inclusion requirement
{http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702}SignedParts: {http://schemas.xmlsoap.org/soap/envelope/}Body not SIGNED
{http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702}EncryptedParts: {http://schemas.xmlsoap.org/soap/envelope/}Body not ENCRYPTED
```

When running with this option:

    mvn exec:java -Dexec.args="confidentiality signencrypt" -Djavax.net.ssl.trustStore=connector.jks

, you will be hitting the https (SSL) URL while providing signed and encrypted information, and see this in your log:

    INFO  [org.switchyard.quickstarts.demo.policy.security.wss.signencrypt.WorkServiceBean] (http--127.0.0.1-8443-1) :: WorkService :: Received work command => CMD-86

When running with this option:

    mvn exec:java -Dexec.args="signencrypt"

, you will be hitting the http (non-SSL) URL while providing signed and encrypted information, and see this in your log:

    INFO  [org.switchyard.quickstarts.demo.policy.security.wss.signencrypt.WorkServiceBean] (http--127.0.0.1-8080-1) :: WorkService :: Received work command => CMD-86

Wait - why did this work? Even though SSL was not used, the content of the message is encrypted, thus the confidentiality policy is still being fulfilled.

Success!
