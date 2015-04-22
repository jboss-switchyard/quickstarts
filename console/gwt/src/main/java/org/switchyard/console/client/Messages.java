package org.switchyard.console.client;

/**
 * Interface to represent the messages contained in resource bundle:
 * 'src/main/java/org/switchyard/console/client/Messages.properties'.
 * 
 * This interface is maintained manually because the i18n goal does not handle
 * number formats (parameters are string).  Also, GWT does not support integer
 * formats.  To maintain, run mvn gwt:i18n.  This will produce a new Messages
 * class in target/generated-sources/gwt.  Merge the newly generated file into
 * this file.
 */
public interface Messages extends com.google.gwt.i18n.client.Messages {
  
  /**
   * Translated "Details...".
   * 
   * @return translated "Details..."
   */
  @DefaultMessage("Details...")
  @Key("button_details")
  String button_details();

  /**
   * Translated "View Configuration...".
   * 
   * @return translated "View Configuration..."
   */
  @DefaultMessage("View Configuration...")
  @Key("button_viewConfiguration")
  String button_viewConfiguration();

  /**
   * Translated "View Details...".
   * 
   * @return translated "View Details..."
   */
  @DefaultMessage("View Details...")
  @Key("button_viewDetails")
  String button_viewDetails();

  /**
   * Translated "<inherited>".
   * 
   * @return translated "<inherited>"
   */
  @DefaultMessage("<inherited>")
  @Key("constant_inherited")
  String constant_inherited();

  /**
   * Translated "Unknown".
   * 
   * @return translated "Unknown"
   */
  @DefaultMessage("Unknown")
  @Key("constant_unknown")
  String constant_unknown();

  /**
   * Translated "Displays details for a specific application.  Select an application to see its implementation details.".
   * 
   * @return translated "Displays details for a specific application.  Select an application to see its implementation details."
   */
  @DefaultMessage("Displays details for a specific application.  Select an application to see its implementation details.")
  @Key("description_applicationDetails")
  String description_applicationDetails();

  /**
   * Translated "Displays a list of deployed SwitchYard applications.  Select an application to see more details.".
   * 
   * @return translated "Displays a list of deployed SwitchYard applications.  Select an application to see more details."
   */
  @DefaultMessage("Displays a list of deployed SwitchYard applications.  Select an application to see more details.")
  @Key("description_applications")
  String description_applications();

  /**
   * Translated "Displays all artifacts referenced throughout the system, along with the applications referencing a specific artifact.".
   * 
   * @return translated "Displays all artifacts referenced throughout the system, along with the applications referencing a specific artifact."
   */
  @DefaultMessage("Displays all artifacts referenced throughout the system, along with the applications referencing a specific artifact.")
  @Key("description_artifactReferences")
  String description_artifactReferences();

  /**
   * Translated "Displays message metrics for a selected reference.".
   * 
   * @return translated "Displays message metrics for a selected reference."
   */
  @DefaultMessage("Displays message metrics for a selected reference.")
  @Key("description_referenceMetrics")
  String description_referenceMetrics();

  /**
   * Translated "Displays details for the selected service.".
   * 
   * @return translated "Displays details for the selected service."
   */
  @DefaultMessage("Displays details for the selected service.")
  @Key("description_serviceDetails")
  String description_serviceDetails();

  /**
   * Translated "Displays message metrics for a selected service.".
   * 
   * @return translated "Displays message metrics for a selected service."
   */
  @DefaultMessage("Displays message metrics for a selected service.")
  @Key("description_serviceMetrics")
  String description_serviceMetrics();

  /**
   * Translated "Displays a list of deployed SwitchYard references.  Select a reference to see more details.".
   * 
   * @return translated "Displays a list of deployed SwitchYard references.  Select a reference to see more details."
   */
  @DefaultMessage("Displays a list of deployed SwitchYard references.  Select a reference to see more details.")
  @Key("description_switchYardReferences")
  String description_switchYardReferences();

  /**
   * Translated "Displays details about the SwitchYard runtime.".
   * 
   * @return translated "Displays details about the SwitchYard runtime."
   */
  @DefaultMessage("Displays details about the SwitchYard runtime.")
  @Key("description_switchYardRuntime")
  String description_switchYardRuntime();

  /**
   * Translated "Displays a list of deployed SwitchYard services.  Select a service to see more details.".
   * 
   * @return translated "Displays a list of deployed SwitchYard services.  Select a service to see more details."
   */
  @DefaultMessage("Displays a list of deployed SwitchYard services.  Select a service to see more details.")
  @Key("description_switchYardServices")
  String description_switchYardServices();

  /**
   * Translated "Displays message metrics for the SwitchYard subsystem.".
   * 
   * @return translated "Displays message metrics for the SwitchYard subsystem."
   */
  @DefaultMessage("Displays message metrics for the SwitchYard subsystem.")
  @Key("description_systemMetrics")
  String description_systemMetrics();

  /**
   * Translated "Could not load all reference metrics.".
   * 
   * @return translated "Could not load all reference metrics."
   */
  @DefaultMessage("Could not load all reference metrics.")
  @Key("error_allReferenceMetricsLoad")
  String error_allReferenceMetricsLoad();

  /**
   * Translated "Could not load all service metrics.".
   * 
   * @return translated "Could not load all service metrics."
   */
  @DefaultMessage("Could not load all service metrics.")
  @Key("error_allServiceMetricsLoad")
  String error_allServiceMetricsLoad();

  /**
   * Translated "Could not load information for application: {0}".
   * @param arg0 application name
   * @return translated "Could not load information for application: {0}"
   */
  @DefaultMessage("Could not load information for application: {0}")
  @Key("error_applicationLoad")
  String error_applicationLoad(String arg0);

  /**
   * Translated "Could not load artifact references.".
   * 
   * @return translated "Could not load artifact references."
   */
  @DefaultMessage("Could not load artifact references.")
  @Key("error_artifactsLoad")
  String error_artifactsLoad();

  /**
   * Translated "Comparison column specified, but no baseline set!".
   * 
   * @return translated "Comparison column specified, but no baseline set!"
   */
  @DefaultMessage("Comparison column specified, but no baseline set!")
  @Key("error_comparisonColumnWithoutBaseline")
  String error_comparisonColumnWithoutBaseline();

  /**
   * Translated "Could not load information for component: {0}".
   * @param arg0 component name
   * @return translated "Could not load information for component: {0}"
   */
  @DefaultMessage("Could not load information for component: {0}")
  @Key("error_componentLoad")
  String error_componentLoad(String arg0);

  /**
   * Translated "Illegal baseline index {0,number} on number of samples {1,number}.".
   * @param arg0 baseline index
   * @param arg1 sample index
   * @return translated "Illegal baseline index {0,number} on number of samples {1,number}."
   */
  @DefaultMessage("Illegal baseline index {0,number} on number of samples {1,number}.")
  @Key("error_illegalBaselineIndex")
  String error_illegalBaselineIndex(int arg0,  int arg1);

  /**
   * Translated "Metric value at index {0,number} is null".
   * @param arg0 index
   * @return translated "Metric value at index {0,number} is null"
   */
  @DefaultMessage("Metric value at index {0,number} is null")
  @Key("error_metricIsNullAtIndex")
  String error_metricIsNullAtIndex(int arg0);

  /**
   * Translated "Cannot reveal application details.  No application specified.".
   * 
   * @return translated "Cannot reveal application details.  No application specified."
   */
  @DefaultMessage("Cannot reveal application details.  No application specified.")
  @Key("error_navigateToApplication")
  String error_navigateToApplication();

  /**
   * Translated "Cannot reveal reference details.  No reference or application specified.".
   * 
   * @return translated "Cannot reveal reference details.  No reference or application specified."
   */
  @DefaultMessage("Cannot reveal reference details.  No reference or application specified.")
  @Key("error_navigateToReference")
  String error_navigateToReference();

  /**
   * Translated "Cannot reveal service details.  No service or application specified.".
   * 
   * @return translated "Cannot reveal service details.  No service or application specified."
   */
  @DefaultMessage("Cannot reveal service details.  No service or application specified.")
  @Key("error_navigateToService")
  String error_navigateToService();

  /**
   * Translated "Could not load information for reference: {0} from application: {1}".
   * @param arg0 reference name
   * @param arg1 application name
   * @return translated "Could not load information for reference: {0} from application: {1}"
   */
  @DefaultMessage("Could not load information for reference: {0} from application: {1}")
  @Key("error_referenceLoad")
  String error_referenceLoad(String arg0,  String arg1);

  /**
   * Translated "Failure resetting metrics for {0}: {1}".
   * @param arg0 entity name
   * @param arg1 error message
   * @return translated "Failure resetting metrics for {0}: {1}"
   */
  @DefaultMessage("Failure resetting metrics for {0}: {1}")
  @Key("error_resetObjectMetrics")
  String error_resetObjectMetrics(String arg0,  String arg1);

  /**
   * Translated "Failure resetting system metrics: {0}".
   * @param arg0 error message
   * @return translated "Failure resetting system metrics: {0}"
   */
  @DefaultMessage("Failure resetting system metrics: {0}")
  @Key("error_resetSystemMetrics")
  String error_resetSystemMetrics(String arg0);

  /**
   * Translated "Could not load information for service: {0} from application: {1}".
   * @param arg0 service name
   * @param arg1 application name
   * @return translated "Could not load information for service: {0} from application: {1}"
   */
  @DefaultMessage("Could not load information for service: {0} from application: {1}")
  @Key("error_serviceLoad")
  String error_serviceLoad(String arg0,  String arg1);

  /**
   * Translated "Could not load metrics for service: {0}".
   * @param arg0 service name
   * @return translated "Could not load metrics for service: {0}"
   */
  @DefaultMessage("Could not load metrics for service: {0}")
  @Key("error_serviceMetricsLoad")
  String error_serviceMetricsLoad(String arg0);

  /**
   * Translated "Failure setting property: {0}".
   * @param arg0 property name
   * @return translated "Failure setting property: {0}"
   */
  @DefaultMessage("Failure setting property: {0}")
  @Key("error_setProperty")
  String error_setProperty(String arg0);

  /**
   * Translated "Failure starting gateway for {0}: {1}".
   * @param arg0 gateway name
   * @param arg1 error message
   * @return translated "Failure starting gateway for {0}: {1}"
   */
  @DefaultMessage("Failure starting gateway for {0}: {1}")
  @Key("error_startGateway")
  String error_startGateway(String arg0,  String arg1);

  /**
   * Translated "Failure stopping gateway for {0}: {1}".
   * @param arg0 gateway name
   * @param arg1 error message
   * @return translated "Failure stopping gateway for {0}: {1}"
   */
  @DefaultMessage("Failure stopping gateway for {0}: {1}")
  @Key("error_stopGateway")
  String error_stopGateway(String arg0,  String arg1);

  /**
   * Translated "Could not load metrics for system".
   * 
   * @return translated "Could not load metrics for system"
   */
  @DefaultMessage("Could not load metrics for system")
  @Key("error_systemMetricsLoad")
  String error_systemMetricsLoad();

  /**
   * Translated "Unknown error".
   * 
   * @return translated "Unknown error"
   */
  @DefaultMessage("Unknown error")
  @Key("error_unknown")
  String error_unknown();

  /**
   * Translated "Failure updating throttling details for {0}: {1}".
   * @param arg0 service name
   * @param arg1 error message
   * @return translated "Failure updating throttling details for {0}: {1}"
   */
  @DefaultMessage("Failure updating throttling details for {0}: {1}")
  @Key("error_updateThrottling")
  String error_updateThrottling(String arg0,  String arg1);

  /**
   * Translated "Actual".
   * 
   * @return translated "Actual"
   */
  @DefaultMessage("Actual")
  @Key("label_actual")
  String label_actual();

  /**
   * Translated "Application".
   * 
   * @return translated "Application"
   */
  @DefaultMessage("Application")
  @Key("label_application")
  String label_application();

  /**
   * Translated "Application Details".
   * 
   * @return translated "Application Details"
   */
  @DefaultMessage("Application Details")
  @Key("label_applicationDetails")
  String label_applicationDetails();

  /**
   * Translated "Application Name".
   * 
   * @return translated "Application Name"
   */
  @DefaultMessage("Application Name")
  @Key("label_applicationName")
  String label_applicationName();

  /**
   * Translated "Application Namespace".
   * 
   * @return translated "Application Namespace"
   */
  @DefaultMessage("Application Namespace")
  @Key("label_applicationNamespace")
  String label_applicationNamespace();

  /**
   * Translated "Applications".
   * 
   * @return translated "Applications"
   */
  @DefaultMessage("Applications")
  @Key("label_applications")
  String label_applications();

  /**
   * Translated "Applications Using Artifact".
   * 
   * @return translated "Applications Using Artifact"
   */
  @DefaultMessage("Applications Using Artifact")
  @Key("label_applicationsUsingArtifacts")
  String label_applicationsUsingArtifacts();

  /**
   * Translated "Artifact References".
   * 
   * @return translated "Artifact References"
   */
  @DefaultMessage("Artifact References")
  @Key("label_artifactReferences")
  String label_artifactReferences();

  /**
   * Translated "Artifacts".
   * 
   * @return translated "Artifacts"
   */
  @DefaultMessage("Artifacts")
  @Key("label_artifacts")
  String label_artifacts();

  /**
   * Translated "Average Processing Time".
   * 
   * @return translated "Average Processing Time"
   */
  @DefaultMessage("Average Processing Time")
  @Key("label_averageProcessingTime")
  String label_averageProcessingTime();

  /**
   * Translated "Average Time".
   * 
   * @return translated "Average Time"
   */
  @DefaultMessage("Average Time")
  @Key("label_averageTime")
  String label_averageTime();

  /**
   * Translated "Component Details".
   * 
   * @return translated "Component Details"
   */
  @DefaultMessage("Component Details")
  @Key("label_componentDetails")
  String label_componentDetails();

  /**
   * Translated "Component Services".
   * 
   * @return translated "Component Services"
   */
  @DefaultMessage("Component Services")
  @Key("label_componentServices")
  String label_componentServices();

  /**
   * Translated "Configuration".
   * 
   * @return translated "Configuration"
   */
  @DefaultMessage("Configuration")
  @Key("label_configuration")
  String label_configuration();

  /**
   * Translated "Core Runtime".
   * 
   * @return translated "Core Runtime"
   */
  @DefaultMessage("Core Runtime")
  @Key("label_coreRuntime")
  String label_coreRuntime();

  /**
   * Translated "Details".
   * 
   * @return translated "Details"
   */
  @DefaultMessage("Details")
  @Key("label_details")
  String label_details();

  /**
   * Translated "Enabled".
   * 
   * @return translated "Enabled"
   */
  @DefaultMessage("Enabled")
  @Key("label_enabled")
  String label_enabled();

  /**
   * Translated "Fault Count".
   * 
   * @return translated "Fault Count"
   */
  @DefaultMessage("Fault Count")
  @Key("label_faultCount")
  String label_faultCount();

  /**
   * Translated "Fault %".
   * 
   * @return translated "Fault %"
   */
  @DefaultMessage("Fault %")
  @Key("label_faultPercent")
  String label_faultPercent();

  /**
   * Translated "From".
   * 
   * @return translated "From"
   */
  @DefaultMessage("From")
  @Key("label_from")
  String label_from();

  /**
   * Translated "Gateway Configuration".
   * 
   * @return translated "Gateway Configuration"
   */
  @DefaultMessage("Gateway Configuration")
  @Key("label_gatewayConfiguration")
  String label_gatewayConfiguration();

  /**
   * Translated "Gateway Metrics".
   * 
   * @return translated "Gateway Metrics"
   */
  @DefaultMessage("Gateway Metrics")
  @Key("label_gatewayMetrics")
  String label_gatewayMetrics();

  /**
   * Translated "Gateways".
   * 
   * @return translated "Gateways"
   */
  @DefaultMessage("Gateways")
  @Key("label_gateways")
  String label_gateways();

  /**
   * Translated "Implementation".
   * 
   * @return translated "Implementation"
   */
  @DefaultMessage("Implementation")
  @Key("label_implementation")
  String label_implementation();

  /**
   * Translated "Implementation Details".
   * 
   * @return translated "Implementation Details"
   */
  @DefaultMessage("Implementation Details")
  @Key("label_implementationDetails")
  String label_implementationDetails();

  /**
   * Translated "{0} implementation".
   * @param arg0 implementation name
   * @return translated "{0} implementation"
   */
  @DefaultMessage("{0} implementation")
  @Key("label_implementationInstance")
  String label_implementationInstance(String arg0);

  /**
   * Translated "Installed Components".
   * 
   * @return translated "Installed Components"
   */
  @DefaultMessage("Installed Components")
  @Key("label_installedComponents")
  String label_installedComponents();

  /**
   * Translated "Interface".
   * 
   * @return translated "Interface"
   */
  @DefaultMessage("Interface")
  @Key("label_interface")
  String label_interface();

  /**
   * Translated "Max. Processing Time".
   * 
   * @return translated "Max. Processing Time"
   */
  @DefaultMessage("Max. Processing Time")
  @Key("label_maxProcessingTime")
  String label_maxProcessingTime();

  /**
   * Translated "Maximum Requests".
   * 
   * @return translated "Maximum Requests"
   */
  @DefaultMessage("Maximum Requests")
  @Key("label_maximumRequests")
  String label_maximumRequests();

  /**
   * Translated "Message Count".
   * 
   * @return translated "Message Count"
   */
  @DefaultMessage("Message Count")
  @Key("label_messageCount")
  String label_messageCount();

  /**
   * Translated "Message Counts".
   * 
   * @return translated "Message Counts"
   */
  @DefaultMessage("Message Counts")
  @Key("label_messageCounts")
  String label_messageCounts();

  /**
   * Translated "Metric".
   * 
   * @return translated "Metric"
   */
  @DefaultMessage("Metric")
  @Key("label_metric")
  String label_metric();

  /**
   * Translated "Min. Processing Time".
   * 
   * @return translated "Min. Processing Time"
   */
  @DefaultMessage("Min. Processing Time")
  @Key("label_minProcessingTime")
  String label_minProcessingTime();

  /**
   * Translated "Name".
   * 
   * @return translated "Name"
   */
  @DefaultMessage("Name")
  @Key("label_name")
  String label_name();

  /**
   * Translated "Namespace".
   * 
   * @return translated "Namespace"
   */
  @DefaultMessage("Namespace")
  @Key("label_namespace")
  String label_namespace();

  /**
   * Translated "Operation Metrics".
   * 
   * @return translated "Operation Metrics"
   */
  @DefaultMessage("Operation Metrics")
  @Key("label_operationMetrics")
  String label_operationMetrics();

  /**
   * Translated "Processing Times".
   * 
   * @return translated "Processing Times"
   */
  @DefaultMessage("Processing Times")
  @Key("label_processingTimes")
  String label_processingTimes();

  /**
   * Translated "Promoted Service".
   * 
   * @return translated "Promoted Service"
   */
  @DefaultMessage("Promoted Service")
  @Key("label_promotedService")
  String label_promotedService();

  /**
   * Translated "Properties".
   * 
   * @return translated "Properties"
   */
  @DefaultMessage("Properties")
  @Key("label_properties")
  String label_properties();

  /**
   * Translated "Raw Configuration".
   * 
   * @return translated "Raw Configuration"
   */
  @DefaultMessage("Raw Configuration")
  @Key("label_rawConfiguration")
  String label_rawConfiguration();

  /**
   * Translated "Reference Details".
   * 
   * @return translated "Reference Details"
   */
  @DefaultMessage("Reference Details")
  @Key("label_referenceDetails")
  String label_referenceDetails();

  /**
   * Translated "Reference Message Metrics".
   * 
   * @return translated "Reference Message Metrics"
   */
  @DefaultMessage("Reference Message Metrics")
  @Key("label_referenceMessageMetrics")
  String label_referenceMessageMetrics();

  /**
   * Translated "Reference Metrics".
   * 
   * @return translated "Reference Metrics"
   */
  @DefaultMessage("Reference Metrics")
  @Key("label_referenceMetrics")
  String label_referenceMetrics();

  /**
   * Translated "Referenced Service Metrics".
   * 
   * @return translated "Referenced Service Metrics"
   */
  @DefaultMessage("Referenced Service Metrics")
  @Key("label_referencedServiceMetrics")
  String label_referencedServiceMetrics();

  /**
   * Translated "References".
   * 
   * @return translated "References"
   */
  @DefaultMessage("References")
  @Key("label_references")
  String label_references();

  /**
   * Translated "Referencing Applications".
   * 
   * @return translated "Referencing Applications"
   */
  @DefaultMessage("Referencing Applications")
  @Key("label_referencingApplications")
  String label_referencingApplications();

  /**
   * Translated "Reset".
   * 
   * @return translated "Reset"
   */
  @DefaultMessage("Reset")
  @Key("label_reset")
  String label_reset();

  /**
   * Translated "Reset All Metrics".
   * 
   * @return translated "Reset All Metrics"
   */
  @DefaultMessage("Reset All Metrics")
  @Key("label_resetAllMetrics")
  String label_resetAllMetrics();

  /**
   * Translated "Reset Metrics".
   * 
   * @return translated "Reset Metrics"
   */
  @DefaultMessage("Reset Metrics")
  @Key("label_resetMetrics")
  String label_resetMetrics();

  /**
   * Translated "Runtime Details".
   * 
   * @return translated "Runtime Details"
   */
  @DefaultMessage("Runtime Details")
  @Key("label_runtimeDetails")
  String label_runtimeDetails();

  /**
   * Translated "Service Details".
   * 
   * @return translated "Service Details"
   */
  @DefaultMessage("Service Details")
  @Key("label_serviceDetails")
  String label_serviceDetails();

  /**
   * Translated "Service Message Metrics".
   * 
   * @return translated "Service Message Metrics"
   */
  @DefaultMessage("Service Message Metrics")
  @Key("label_serviceMessageMetrics")
  String label_serviceMessageMetrics();

  /**
   * Translated "Service Metrics".
   * 
   * @return translated "Service Metrics"
   */
  @DefaultMessage("Service Metrics")
  @Key("label_serviceMetrics")
  String label_serviceMetrics();

  /**
   * Translated "Service Operation Metrics".
   * 
   * @return translated "Service Operation Metrics"
   */
  @DefaultMessage("Service Operation Metrics")
  @Key("label_serviceOperationMetrics")
  String label_serviceOperationMetrics();

  /**
   * Translated "Services".
   * 
   * @return translated "Services"
   */
  @DefaultMessage("Services")
  @Key("label_services")
  String label_services();

  /**
   * Translated "Start".
   * 
   * @return translated "Start"
   */
  @DefaultMessage("Start")
  @Key("label_start")
  String label_start();

  /**
   * Translated "Start/Stop".
   * 
   * @return translated "Start/Stop"
   */
  @DefaultMessage("Start/Stop")
  @Key("label_startStop")
  String label_startStop();

  /**
   * Translated "Status".
   * 
   * @return translated "Status"
   */
  @DefaultMessage("Status")
  @Key("label_status")
  String label_status();

  /**
   * Translated "Stop".
   * 
   * @return translated "Stop"
   */
  @DefaultMessage("Stop")
  @Key("label_stop")
  String label_stop();

  /**
   * Translated "Success Count".
   * 
   * @return translated "Success Count"
   */
  @DefaultMessage("Success Count")
  @Key("label_successCount")
  String label_successCount();

  /**
   * Translated "SwitchYard Applications".
   * 
   * @return translated "SwitchYard Applications"
   */
  @DefaultMessage("SwitchYard Applications")
  @Key("label_switchYardApplications")
  String label_switchYardApplications();

  /**
   * Translated "SwitchYard Artifact References".
   * 
   * @return translated "SwitchYard Artifact References"
   */
  @DefaultMessage("SwitchYard Artifact References")
  @Key("label_switchYardArtifactReferences")
  String label_switchYardArtifactReferences();

  /**
   * Translated "SwitchYard Message Metrics".
   * 
   * @return translated "SwitchYard Message Metrics"
   */
  @DefaultMessage("SwitchYard Message Metrics")
  @Key("label_switchYardMessageMetrics")
  String label_switchYardMessageMetrics();

  /**
   * Translated "SwitchYard References".
   * 
   * @return translated "SwitchYard References"
   */
  @DefaultMessage("SwitchYard References")
  @Key("label_switchYardReferences")
  String label_switchYardReferences();

  /**
   * Translated "SwitchYard Runtime".
   * 
   * @return translated "SwitchYard Runtime"
   */
  @DefaultMessage("SwitchYard Runtime")
  @Key("label_switchYardRuntime")
  String label_switchYardRuntime();

  /**
   * Translated "SwitchYard Runtime Details".
   * 
   * @return translated "SwitchYard Runtime Details"
   */
  @DefaultMessage("SwitchYard Runtime Details")
  @Key("label_switchYardRuntimeDetails")
  String label_switchYardRuntimeDetails();

  /**
   * Translated "SwitchYard Services".
   * 
   * @return translated "SwitchYard Services"
   */
  @DefaultMessage("SwitchYard Services")
  @Key("label_switchYardServices")
  String label_switchYardServices();

  /**
   * Translated "System".
   * 
   * @return translated "System"
   */
  @DefaultMessage("System")
  @Key("label_system")
  String label_system();

  /**
   * Translated "System Message Metrics".
   * 
   * @return translated "System Message Metrics"
   */
  @DefaultMessage("System Message Metrics")
  @Key("label_systemMessageMetrics")
  String label_systemMessageMetrics();

  /**
   * Translated "Target Namespace".
   * 
   * @return translated "Target Namespace"
   */
  @DefaultMessage("Target Namespace")
  @Key("label_targetNamespace")
  String label_targetNamespace();

  /**
   * Translated "Throttling".
   * 
   * @return translated "Throttling"
   */
  @DefaultMessage("Throttling")
  @Key("label_throttling")
  String label_throttling();

  /**
   * Translated "Time %".
   * 
   * @return translated "Time %"
   */
  @DefaultMessage("Time %")
  @Key("label_timePercent")
  String label_timePercent();

  /**
   * Translated "Time Period (millis)".
   * 
   * @return translated "Time Period (millis)"
   */
  @DefaultMessage("Time Period (millis)")
  @Key("label_timePeriod")
  String label_timePeriod();

  /**
   * Translated "To".
   * 
   * @return translated "To"
   */
  @DefaultMessage("To")
  @Key("label_to")
  String label_to();

  /**
   * Translated "Total Count".
   * 
   * @return translated "Total Count"
   */
  @DefaultMessage("Total Count")
  @Key("label_totalCount")
  String label_totalCount();

  /**
   * Translated "Total Processing Time".
   * 
   * @return translated "Total Processing Time"
   */
  @DefaultMessage("Total Processing Time")
  @Key("label_totalProcessingTime")
  String label_totalProcessingTime();

  /**
   * Translated "Transformers".
   * 
   * @return translated "Transformers"
   */
  @DefaultMessage("Transformers")
  @Key("label_transformers")
  String label_transformers();

  /**
   * Translated "Type".
   * 
   * @return translated "Type"
   */
  @DefaultMessage("Type")
  @Key("label_type")
  String label_type();

  /**
   * Translated "URL".
   * 
   * @return translated "URL"
   */
  @DefaultMessage("URL")
  @Key("label_url")
  String label_url();

  /**
   * Translated "Validators".
   * 
   * @return translated "Validators"
   */
  @DefaultMessage("Validators")
  @Key("label_validators")
  String label_validators();

  /**
   * Translated "Version".
   * 
   * @return translated "Version"
   */
  @DefaultMessage("Version")
  @Key("label_version")
  String label_version();
}
