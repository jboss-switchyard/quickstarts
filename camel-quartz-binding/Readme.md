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
11:21:11,024 INFO  [stdout] (DefaultQuartzScheduler-camel-3_Worker-2) 1. Service executed.
11:21:12,006 INFO  [stdout] (DefaultQuartzScheduler-camel-3_Worker-3) 2. Service executed.
11:21:13,005 INFO  [stdout] (DefaultQuartzScheduler-camel-3_Worker-4) 3. Service executed.
11:21:14,005 INFO  [stdout] (DefaultQuartzScheduler-camel-3_Worker-5) 4. Service executed.
11:21:15,004 INFO  [stdout] (DefaultQuartzScheduler-camel-3_Worker-6) 5. Service executed.
11:21:16,005 INFO  [stdout] (DefaultQuartzScheduler-camel-3_Worker-7) 6. Service executed.
11:21:17,007 INFO  [stdout] (DefaultQuartzScheduler-camel-3_Worker-8) 7. Service executed.
11:21:18,008 INFO  [stdout] (DefaultQuartzScheduler-camel-3_Worker-9) 8. Service executed.


## Further Reading

1. [Quartz Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/Quartz)
