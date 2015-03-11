# EAR Deployment Quickstart

This quickstart provides an example packaging multiple SwitchYard applications, associated libraries, and a JMS destination inside an EAR archive.  The quickstart consists of the following pieces:

* artifacts : contains XSDs, WSDLs, and Java domain objects which are used by service providers and consumers across application projects
* order-service : provides two services - OrderService and InventoryService
* order-consumer : consumes OrderService through a SOAP/HTTP binding
* ear-assembly : packages all of the above into an EAR archive


## Deploying the EAR

Preqrequisites 
==============
Maven


Running the quickstart
======================


EAP
----------
1. Start EAP in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Create an application user:

        ${AS}/bin/add-user.sh --user guest --password guestp.1 --realm ApplicationRealm --group guest
        
3. Build and deploy the EAR

        mvn install -Pdeploy

4. Test the application

        mvn exec:java

5. Undeploy the EAR

        mvn clean -Pdeploy


Wildfly
----------
1. Start Wildfly in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Create an application user:

        ${AS}/bin/add-user.sh --user guest --password guestp.1 --realm ApplicationRealm --group guest

3. Build and deploy the EAR

        mvn install -Pdeploy -Pwildfly

4. Test the application

        mvn exec:java -Pwildfly

5. Undeploy the EAR

        mvn clean -Pdeploy -Pwildfly

