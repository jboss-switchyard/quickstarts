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

JBoss AS 7
----------
1. Start JBoss AS 7 in standalone mode:

        ${AS}/bin/standalone.sh

2. Build and deploy the Quickstart :

        mvn install -Pdeploy

3. 
<br/>
```
           Copy src/main/resources/file.txt to /tmp/inbox/file.txt  
```
<br/>
* (If on Windows, change the file binding in switchyard.xml to a Windows directory path)


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

4. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [Camel Bindy](http://camel.apache.org/bindy.html)
