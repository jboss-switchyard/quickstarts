Introduction
============
This quickstart demonstrates the usage of the Camel Component and it's binding feature, by invoking 
service periodically.

Running the quickstart
======================


EAP
----------
1. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy

3. Check the server console for output from the service. By default after every second
   message should be printed

4. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Build and deploy the quickstart

        mvn install -Pdeploy -Pwildfly

3. Check the server console for output from the service. By default after every second
message should be printed

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the quartz-binding quickstart :

karaf@root> features:install switchyard-quickstart-camel-quartz-binding

4. Check the server console for output from the service. By default after every second
message should be printed

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-quartz-binding



Expected Output
=================
13:33:05,001 | INFO  | camel-2_Worker-9 | GreetingServiceBean              | 311 - org.switchyard.quickstarts.switchyard.camel.quartz.binding - 2.0.0.SNAPSHOT | 1. Service executed.
13:33:06,001 | INFO  | camel-2_Worker-1 | GreetingServiceBean              | 311 - org.switchyard.quickstarts.switchyard.camel.quartz.binding - 2.0.0.SNAPSHOT | 2. Service executed.
13:33:07,002 | INFO  | camel-2_Worker-2 | GreetingServiceBean              | 311 - org.switchyard.quickstarts.switchyard.camel.quartz.binding - 2.0.0.SNAPSHOT | 3. Service executed.
13:33:08,001 | INFO  | camel-2_Worker-3 | GreetingServiceBean              | 311 - org.switchyard.quickstarts.switchyard.camel.quartz.binding - 2.0.0.SNAPSHOT | 4. Service executed.
13:33:09,001 | INFO  | camel-2_Worker-4 | GreetingServiceBean              | 311 - org.switchyard.quickstarts.switchyard.camel.quartz.binding - 2.0.0.SNAPSHOT | 5. Service executed.
13:33:10,002 | INFO  | camel-2_Worker-5 | GreetingServiceBean              | 311 - org.switchyard.quickstarts.switchyard.camel.quartz.binding - 2.0.0.SNAPSHOT | 6. Service executed.
13:33:11,001 | INFO  | camel-2_Worker-6 | GreetingServiceBean              | 311 - org.switchyard.quickstarts.switchyard.camel.quartz.binding - 2.0.0.SNAPSHOT | 7. Service executed.
13:33:12,001 | INFO  | camel-2_Worker-7 | GreetingServiceBean              | 311 - org.switchyard.quickstarts.switchyard.camel.quartz.binding - 2.0.0.SNAPSHOT | 8. Service executed.
13:33:13,002 | INFO  | camel-2_Worker-8 | GreetingServiceBean              | 311 - org.switchyard.quickstarts.switchyard.camel.quartz.binding - 2.0.0.SNAPSHOT | 9. Service executed.




## Further Reading

1. [Quartz Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Quartz)
