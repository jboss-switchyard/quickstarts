Introduction
============
This quickstart demonstrates the usage of the Camel HL7 component within a
SwitchYard service.  A tcp endpoint listens for requests and routes them to 
the service, which prints out parts of the request to the console. 

Running the quickstart
======================


EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. Submit a request with test client.

        mvn exec:java

4. Watch for the expected output (see "Expected Output" heading) in the console.

5. To uninstall :

        mvn clean -Pdeploy



Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy -Pwildfly

3. Submit a request with test client.

         mvn exec:java

4. Watch for the expected output (see "Expected Output" heading) in the console.

5. To uninstall :

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0):

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the camel-hl7 quickstart :

karaf@root> features:install switchyard-quickstart-camel-hl7

4. Submit a request with test client.

mvn exec:java

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-hl7


Expected Output
===============
```
12:35:01,274 INFO  [stdout] (pool-7-thread-1) Query Date/Time : 200612211200
12:35:01,274 INFO  [stdout] (pool-7-thread-1) Query Format Code : R
12:35:01,275 INFO  [stdout] (pool-7-thread-1) Query Priority : I
12:35:01,275 INFO  [stdout] (pool-7-thread-1) Query ID : GetPatient
12:35:01,275 INFO  [stdout] (pool-7-thread-1) Deferred Response Type : null
12:35:01,275 INFO  [stdout] (pool-7-thread-1) Deferred Response Date/Time : null
12:35:01,276 INFO  [stdout] (pool-7-thread-1) Quantity Limited Request : 1
12:35:01,276 INFO  [stdout] (pool-7-thread-1) Query Results Level : null
```

4. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [Camel HL7](http://camel.apache.org/hl7.html)
