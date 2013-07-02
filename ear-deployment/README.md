# EAR Deployment Quickstart

This quickstart provides an example packaging multiple SwitchYard applications, associated libraries, and a JMS destination inside an EAR archive.  The quickstart consists of the following pieces:

* artifacts : contains XSDs, WSDLs, and Java domain objects which are used by service providers and consumers across application projects
* order-service : provides two services - OrderService and InventoryService
* order-consumer : consumes OrderService through a SOAP/HTTP binding
* ear-asesmbly : packages all of the above into an EAR archive


## Deploying the EAR

1. Build the entire quickstart from the root of the ear-deployment module:

        mvn clean install
    
2. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

3. Add a JMS user using add-user.sh with username=guest, password=guestp.1, Realm=ApplicationRealm, roles=guest

        ${AS}/bin/add-user.sh
        
4. Deploy the EAR

        cd ear-deployment/ear-assembly
        mvn jboss-as:deploy


## Testing the application

1. Navigate to the order-consumer project

        cd ear-deployment/order-consumer

2. Execute the test client

        mvn exec:java

3. Examine output returned from server.

