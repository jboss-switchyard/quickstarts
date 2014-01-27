bpel-jms-binding: demonstrates the use of a JMS binding for a BPEL service
============================================
Author: SwitchYard Team  
Level: Beginner  
Technologies: SwitchYard, BPEL, Camel  
Summary: Demonstrates the use of a JMS binding for a BPEL service. 
Target Product: FSW  
Source: <https://github.com/jboss-switchyard/quickstarts>

What is it?
-----------
This quickstart demonstrates the use of a JMS binding for a BPEL service.  Also of
note : the Camel JMS binding in the quickstart uses WSDL as it's interface type
instead of Java.


![BPEL JMS Binding Quickstart](https://github.com/jboss-switchyard/quickstarts/raw/master/bpel-service/jms_binding/bpel-jms-binding.jpg)



System requirements
-------------------
Before building and running this quick start you need:

* Maven 3.0.3 or higher
* JDK 1.6 or 1.7
* JBoss AS 7


Build and Deploy the Quickstart
-------------------------
1. Start JBoss AS 7 in standalone-full mode:

        `${AS}/bin/standalone.sh --server-config=standalone-full.xml`

2. Add JMS user using add-user.sh with username=guest, password=guestp.1, Realm=ApplicationRealm, role=guest

        `${AS}/bin/add-user.sh`

3. Deploy the quickstart with

        `mvn install -Pdeploy`

4. Execute HornetQClient

        `mvn exec:java`

Use the bundle
-------------------
Successful case will produce the following output:

```
Message sent. Waiting for reply ...
REPLY: 
<sayHelloResponse xmlns="http://www.jboss.org/bpel/examples">
  <tns:result xmlns:tns="http://www.jboss.org/bpel/examples">Hello Skippy</tns:result>
</sayHelloResponse>
```
Undeploy the bundle
-------------------

Undeploy the quickstart:
        `mvn clean -Pdeploy`