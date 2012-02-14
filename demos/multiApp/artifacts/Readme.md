# MultiApp Demo - Artifacts

The artifacts project contains service artifacts used by service consumers and providers in the MultiApp demo.  The following artifacts are included:

* orderTypes.xsd : XML schema containing data types used in the OrderService WSDL
* OrderService.wsdl : WSDL for OrderService
* Domain classes : Java domain objects used in the public Java interface of the InventoryService (found in src/main/java).

Building this project will produce two artifacts:

* OrderService.jar : complete, deployable service artifact module containing all XSDs, WSDLs, and Java classes
* OrderService-classes.jar : contains only the Java domain objects in the artifact project

If you are using a service repository with this application, the XSD, WSDL, and OrderService-classes.jar can be uploaded into Guvnor and packaged as a single, deployable service module.  

