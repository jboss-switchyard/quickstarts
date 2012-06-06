Introduction
============
This quickstart demonstrates how policy can be used to control the security characteristics
of a service invocation.  The only service in the application is a Bean service called
"WorkService".


Running the quickstart
======================

1. Build the quickstart:
    mvn clean install
2. Create a keystore to support SSL:
    cd ${AS7}/standalone/configuration
    keytool -genkey -alias tomcat -keyalg RSA -keypass changeit -keystore tomcat.keystore
    (password is "changeit")
3. Add the "https" connector to the web subsystem to support SSL:
    <subsystem xmlns="urn:jboss:domain:web:1.1" default-virtual-server="default-host" native="false">
        <connector name="http" protocol="HTTP/1.1" scheme="http" socket-binding="http"/>
        <connector name="https" protocol="HTTP/1.1" scheme="https" socket-binding="https" secure="true">
            <ssl name="https" password="changeit" certificate-key-file="../standalone/configuration/tomcat.keystore"/>
        </connector>
        ...
    </subsystem>
4. Start JBoss AS 7 in standalone mode:
    ./standalone.sh
5. Deploy the quickstart
    cp target/switchyard-quickstart-demo-policy-security.jar ${AS7}/standalone/deployments
6. Execute the test
    See "Options" section below.
7. Check the server console for output from the service.
8. Undeploy the application
    rm ${AS7}/standalone/deployments/switchyard-quickstart-demo-policy-security-{version}.jar.deployed


Options
=======

When running with no options:

    mvn exec:java

, you will be hitting the http (non-SSL) URL, and see this in your log:

    ERROR [org.switchyard.internal.DefaultHandlerChain] (http--127.0.0.1-8080-1) org.switchyard.HandlerException was thrown by handler(generic-policy): Required policy has not been provided: confidentiality

When running with this option:

    mvn exec:java -Dexec.args="confidentiality"

, you will be hitting the https (SSL-enabled) URL, and see this in your log:

    INFO  [org.switchyard.quickstarts.demo.policy.security.WorkServiceBean] (http--127.0.0.1-8443-1) :: WorkService :: Received work command => CMD-1338989015873

