# MultiApp Demo Quickstart

This quickstart provides an example of a multi-project application structure with SwitchYard.  The quickstart consists of the following pieces:

* artifacts : contains XSDs, WSDLs, and Java domain objects which are used by service providers and consumers across application projects
* order-service : provides two services - OrderService and InventoryService
* order-consumer : consumes OrderService through a SOAP/HTTP binding
* web : consumes InventoryService using it's Java service interface

The MultiApp quickstart can also be used to demonstrate design-time repository integration with SwitchYard.  Individual service artifacts in the artifacts project can be uploaded to a service repository (e.g. Guvnor) and exported as a service module for use within projects which consume the service.  Additional detail can be found in the SwitchYard Repository Integration wiki article.

Consult the README.md in each individual project for more info.

## Running the Example

1. Deploy each of the following to a SwitchYard AS7 runtime:
    * multiApp/artifacts/target/OrderService.jar
    * multiApp/order-service/target/switchyard-quickstart-demo-multi-order-service.jar
    * multiApp/order-consumer/target/switchyard-quickstart-demo-multi-order-consumer.jar
    * multiApp/web/target/switchyard-quickstart-demo-multi-web.war
    
2. Use one or both of the consuming application projects:
    * <b>Web</b>: Visit <http://localhost:8080/switchyard-quickstart-demo-multi-web>.
    * <b>JMS</b>: Use 'mvn exec:java' in the order-consumer project to submit a JMS order message via the OrderIntake service.

## Further Reading

1. [SwitchYard Repository Integration](https://community.jboss.org/wiki/SwitchYardRepositoryIntegration)
