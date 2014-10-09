Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by sending 
data through TCP/UDP. All data sent to port 3939 and 3940 should be displayed in AS console.

Running the quickstart
======================


EAP
----------

1. Start EAP in standalone-full mode:

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


Wildfly
----------

1. Start Wildfly in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart:

        mvn install -Pdeploy -Pwildfly

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

        mvn clean -Pdeploy -Pwildfly


Karaf
-----
Instead of steps 1-2,7 above for JBoss AS 7...

1. Create a ${KARAF}/quickstarts/camel-netty-binding/ directory, and copy users.jks and roles.properties into it.

2. Start the Karaf server :

${KARAF_HOME}/bin/karaf

3. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

4. Install the feature for the camel-netty-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-netty-binding

5. Execute client and send text message :
<br/>
```
mvn exec:java -Pudp -Pkaraf
```
<br/>

6. Check the log for the expected results :

karaf@root> log:display

6. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-netty-binding


Expected Results
=================
2014-10-07 11:17:23,819 | INFO  | ttyOrderedWorker | DefaultGreetingServiceBean       | etty.binding.GreetingServiceBean   54 | 169 - org.switchyard.quickstarts.switchyard-camel-netty-binding - 2.0.0.Alpha3 | :: DefaultGreetingService :: Hello foobar! (caller principal=null, in roles? 'friend'=false 'enemy'=false)


## Further Reading

1. [TCP UDP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/TCP+UDP)
