# MultiApp Demo Quickstart

This quickstart provides an example of a multi-project application structure with SwitchYard.  The quickstart consists of the following pieces:

* artifacts : contains XSDs, WSDLs, and Java domain objects which are used by service providers and consumers across application projects
* order-service : provides two services - OrderService and InventoryService
* order-consumer : consumes OrderService through a SOAP/HTTP binding
* web : consumes InventoryService using it's Java service interface

The MultiApp quickstart can also be used to demonstrate design-time repository integration with SwitchYard.  Individual service artifacts in the artifacts project can be uploaded to a service repository (e.g. Guvnor) and exported as a service module for use within projects which consume the service.  Additional detail can be found in the SwitchYard Repository Integration wiki article.

Consult the README.md in each individual project for more info.

## Running the Example

1. Start JBoss AS 7 in standalone-full mode:

        ${AS}/bin/standalone.sh --server-config=standalone-full.xml

2. Create an application user:

        ${AS}/bin/add-user.sh --user guest --password guestp.1 --realm ApplicationRealm --group guest

3. Build and deploy the quickstart

        mvn install -Pdeploy

4. Use one or both of the consuming application projects:
    * <b>Web</b>: Visit <http://localhost:8080/switchyard-quickstart-demo-multi-web>.
    * <b>JMS</b>: Use 'mvn exec:java' in the order-consumer project to submit a JMS order message via the OrderIntake service.

5. Check the server console for output from the service.

6. Undeploy the quickstart:

        mvn clean -Pdeploy

## Further Reading

1. [SwitchYard Repository Integration](https://community.jboss.org/wiki/SwitchYardRepositoryIntegration)
