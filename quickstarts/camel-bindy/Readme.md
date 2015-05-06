Introduction
============
This quickstart demonstrates the usage of the Camel Bindy component within a
SwitchYard service.  The service unmarshals a delimited String into Order
objects, and then makes two changes to the Orders - it sets the
price of any "Lucky Charms" order to 17, and it changes any "Grape Nuts" 
orders to "Cheerios".

This example is invoked through a File gateway binding. 

Running the quickstart
======================


EAP
----------
1. Start EAP in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. 
<br/>
```
        Copy src/test/resources/file.txt to /tmp/inbox/file.txt  
```
<br/>
* (If on Windows, change the file binding in switchyard.xml to a Windows directory path)

4. Undeploy the quickstart:

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy -Pwildfly

3. 
<br/>
```
        Copy src/test/resources/file.txt to /tmp/inbox/file.txt  
```
<br/>
* (If on Windows, change the file binding in switchyard.xml to a Windows directory path)

4. Undeploy the quickstart:

        mvn clean -Pdeploy -Pwildfly


Karaf
----------
1. Start the Karaf server :

${KARAF_HOME}/bin/karaf

2. Add the features URL for the respective version of SwitchYard.   Replace {SWITCHYARD-VERSION}
with the version of SwitchYard that you are using (ex. 2.0.0): 

karaf@root> features:addurl mvn:org.switchyard.karaf/switchyard/{SWITCHYARD-VERSION}/xml/features

3. Install the feature for the camel-bindy quickstart :

karaf@root> features:install switchyard-quickstart-camel-bindy

4. 
<br/>
```
Copy src/test/resources/file.txt to /tmp/inbox/file.txt  
```
<br/>

5. Undeploy the quickstart:

karaf@root> features:uninstall switchyard-quickstart-camel-bindy


Expected Output
===============
```
[FileProcessorBean] 1|Fruit Loops|3.99
2|Lucky Charms|4.99
3|Grape Nuts|2.33

Processed Message : 1|Fruit Loops|3.99
2|Lucky Charms|17
3|Cheerios|2.33
```


## Further Reading

1. [Camel Bindy](http://camel.apache.org/bindy.html)
