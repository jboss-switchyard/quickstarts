Welcome to the SwitchYard Quickstart Repository!
================================================

In order to use any of these quickstarts you will need to the following repository to your maven settings file (~/.m2/settings.xml by default):

        <repository>
            <id>jboss-enterprise-repository-group</id>
            <name>JBoss Enterprise Maven Repository Group</name>
            <url>http://maven.repository.redhat.com/techpreview/all/</url>
            <layout>default</layout>
            <releases>
                <enabled>true</enabled>
                <updatePolicy>never</updatePolicy>
            </releases>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>never</updatePolicy>
            </snapshots>
        </repository>

Alternatively, you can use the settings.xml file provided

Quickstarts
-----------
* __bean-service__ : CDI-based bean services, Java transformations, and a SOAP binding.
* __bpel-jms-binding__ : use of a JMS binding for a BPEL service
* __bpel-loan-approval__ : multpile BPEL processes interacting with each other
* __bpel-say-hello__ : BPEL process reads message passed as input
* __bpel_service__ : Examples to demonstrate use of the BPEL component.
* __bpel-simple-correlation__ : two separate interactions correlated to same BPEL process instance
* __bpel-xts-subordinate-wsba__ : BPEL process requiring WS-BusinessActivity
* __bpel-xts-wsat__ : use of WS-AtomicTransaction in BPEL processes
* __bpm-service__ : basic BPMN2 process which invokes bean services.
* __camel-amqp-binding__ : service binding using the Camel amqp component
* __camel-atom-binding__ : service binding using the Camel atom component
* __camel-binding__ : service binding using the Camel gateway component.
* __camel-bindy__ : usage of the Camel Bindy component
* __camel-bus-cdi__ : usage of custom ProcessorFactory used by Camel Exchange Bus internally to create and it's binding feature
* __camel-cxf__ : usage of SOAP with Camel Cxf component
* __camel-ftp-binding__ : ervice binding using the Camel FTP component.
* __camel-hl7__ : usage of the Camel HL7 component within a SwitchYard service
* __camel-jaxb__ :  demonstrates the usage of the camel dataformats
* __camel-jms-binding__ : service binding using a Camel JMS endpoint.
* __camel-mail-binding__ : usage of the Camel Mail Component and its binding feature by receiving and sending messages
* __camel-mqtt-binding__ : usage of the Camel MQTT binding feature
* __camel_netty_binding__ : service binding using a Camel TCP/UDP endpoint.
* __camel_quartz_binding__ : invoke a service periodically.
* __camel_rest_binding__ : service binding using REST.
* __camel-rss-binding__ : usage of the Camel RSSComponent and its binding feature by polling for messages
* __camel-sap-binding__ : usage of the Camel SAP and it's binding feature
* __camel-saxon__ : usage of the Camel XQuery component within a
SwitchYard service
* __camel-service__ : routing service example using the Java DSL and XML route definition languages.
* __camel-soap-proxy__ : simple pass through between SOAP gateways to demonstrate proxy capabilities.
* __camel-sql-binding__ : usage of the Camel SQL Component
* __ear-deployment__ : example packaging multiple SwitchYard applications, associated libraries, and a JMS destination inside an EAR archive
* __http-binding__ : service binding using the HTTP gateway component.
* __jca-inflow-activemq__ : usage of the JCA Component and its service binding feature, by binding to a ActiveMQ Queue
* __jca_inflow_hornetq__ : service binding using the JCA component
* __jca-outbound-activemq__ : usage of the JCA Component and its reference binding feature by binding to a ActiveMQ Queue
* __jca_outbound_hornetq__ : service binding using the JCA component
* __remote-invoker__ : use of the RemoteInvoker with services in SwitchYard using the SCA Binding
* __rest_binding__ : service binding using RESTEasy
* __rules-camel-cbr__ : uses the combination of a rules service and a camel service to create a content-based router.
* __rules-interview__ : demonstrates the use of simple business rules using drools drl.
* __rules-interview-container__ : usage of a rules service which performs an age check
* __rules-interview-dtable__ : usage of a rules service where the rules are specified using a DTABLE resource
* __soap-addressing__ : usage of SOAP with WS-Addressing
* __soap-attachment__ : usage of SOAP with Attachments (SwA)
* __soap-binding-rpc__ : usage of SOAP gateway component using RPC style
* __soap-mtom__: usage of SOAP with MTOM/XOP
* __transform-dozer__ : transformation using Dozer bean mapper.
* __transform-jaxb__ : automatic transformation of JAXB-annotated message payloads.
* __transform-json__ : transformation to/from JSON using the JSON transformer.
* __transform-smooks__ : Smooks-based transformers to map between XML and Java.
* __transform-xslt__ : transformation using XSLT.
* __validate-xml__ : XML message contents validation.


Quickstart Demos
----------------
* __helpdesk__ : an example web application demonstrating features of BPM and SOAP.
* __orders__ : includes a JSF front-end demonstrating injection of service references into webapps.
* __policy-transaction__ : uses transaction policy to control transactional characteristics of a service.
* __webapp-deploy__ : demonstrates deployment of SwitchYard embedded in a web application.
