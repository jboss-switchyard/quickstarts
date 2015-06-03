# MultiApp Demo - Web

The MultiApp web project contains a JSF and CDI-based web application which interacts with the InventoryService provided by the order-service module.

## Using the Web Application

* The artifacts and order-service deployments must be present before deploying the web application
* Deploy switchyard-quickstart-demo-multi-web.war to the SwitchYard AS7 runtime
* Visit <http://localhost:8080/switchyard-quickstart-demo-multi-web>

Submitting the form will update the in-memory inventory DB used by InventoryService.
