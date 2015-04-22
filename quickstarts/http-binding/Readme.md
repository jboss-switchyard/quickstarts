Introduction
============
This quickstart demonstrates the usage of HTTP gateway component. It binds two SwitchYard services
over HTTP URLs that can be accessed by any HTTP based client. One of them also acts as a client.
When a message arrives to the Quote service Symbol service will be invoked via reference binding.

```
+-----------------+      +--------------+      +----------------+      +---------------+
| http://         | ---- | QuoteService | ---- | http://        | ---- | SymbolService |
+-----------------+      +--------------+      +----------------+      +---------------+
```

![HTTP Binding Quickstart](Tooling image comming soon)


Running the quickstart
======================

EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart

        mvn install -Pdeploy

3. Open a console windows and type

        mvn exec:java -Dexec.args="vineyard"

4. You should see the following output

Request: http://localhost:8080/http-binding/quote -> vineyard
Response: 136.5

5. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the quickstart

        mvn install -Pdeploy -Pwildfly

3. Open a console windows and type

        mvn exec:java -Dexec.args="vineyard"

4. You should see the following output

Request: http://localhost:8080/http-binding/quote -> vineyard
Response: 136.5

5. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the http-binding quickstart :

karaf@root> features:install switchyard-quickstart-http-binding

4. To submit a HTTP request to invoke the SOAP gateway, run the quickstart client :
<br/>
```
mvn exec:java -Pkaraf -Dexec.args="vineyard"
```
<br/>

5. You should see the following output

Request: http://localhost:8080/http-binding/quote -> vineyard
Response: 136.5

6. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-http-binding


## Further Reading

1. [HTTP Binding Documentation](https://docs.jboss.org/author/display/SWITCHYARD/HTTP)
