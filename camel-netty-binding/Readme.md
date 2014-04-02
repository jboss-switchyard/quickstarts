Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by sending 
data through TCP/UDP. All data sent to port 3939 and 3940 should be displayed in AS console.

Running the quickstart
======================

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart:

        mvn install -Pdeploy

3. Execute client and send text message:

        mvn exec:java -Pudp

4. Check the server console for output from the service:

        :: DefaultGreetingService :: Hello <your text>! (caller principal=null, in roles? 'friend'=false 'enemy'=false)

   (Because the DefaultGreetingService is not secured, you will see the null principal, and false for both security roles.)

To test TCP: 

5. Execute client and send text message:

        mvn exec:java

6. Check the server console for output from the service:

        :: SecuredGreetingService :: Hello <your text>! (caller principal=UserPrincipal@1741605094[name=kermit], in roles? 'friend'=true 'enemy'=false)

   (Because the SecuredGreetingService is secured, you will see the not-null principal, and true for the expected security role.)

7. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [TCP UDP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/TCP+UDP)
