Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by sending 
data through TCP/UDP. All data sent to port 3939 and 3940 should be displayed in AS console.

Running the quickstart
======================

JBoss AS 7
----------
1. Build the quickstart:

        mvn clean install

2. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Deploy the quickstart

        mvn jboss-as:deploy

4. Execute client and send text message:

        mvn exec:java -Pudp

5. Check the server console for output from the service:

        :: DefaultGreetingService :: Hello <your text>! (caller principal=null, in roles? 'friend'=false 'enemy'=false)

   (Because the DefaultGreetingService is not secured, you will see the null principal, and false for both security roles.)

To test TCP you will need few additional steps. Stop server if it's running.

1. Add to ${AS}/standalone/configuration/standalone.xml configuration a new security domain (look for subsystem with ID urn:jboss:domain:security:1.1)

        <security-domain name="netty-security-cert" cache-type="default">
            <authentication>
                <login-module code="org.switchyard.security.login.CertificateLoginModule" flag="required">
                    <module-option name="keyStoreLocation" value="${jboss.server.config.dir}/users.jks"/>
                    <module-option name="keyStorePassword" value="changeit"/>
                    <module-option name="rolesProperties" value="${jboss.server.config.dir}/roles.properties"/>
                </login-module>
            </authentication>
        </security-domain>

2. Copy users.jks and roles.properties to ${AS}/standalone/configuration.
3. Start server
4. Execute client and send text message:

        mvn exec:java

    :: SecuredGreetingService :: Hello <your text>! (caller principal=UserPrincipal@1741605094[name=kermit], in roles? 'friend'=true 'enemy'=false)

   (Because the SecuredGreetingService is secured, you will see the not-null principal, and true for the expected security role.)

5. Check the server console for output from the service:

        'Secured: Hello there <your text>';

## Further Reading

1. [TCP UDP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/TCP+UDP)
