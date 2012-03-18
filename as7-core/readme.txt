This project bundles the existing AS7 console as a GWT module.

This project also includes minor updates which allow the core console to be
extended (e.g. adding items to the subsystem navigation tree).

This project will be removed once the extension mechanisms and module packaging
are available in an AS7 console release.

The following is a list of modifications to the underlying AS7 console source:
src/main/java/org/jboss/as/console/client/Console.java
	- modified "MODULES" initialization to:
		public final static CoreUI MODULES = GWT.<GinjectorSingleton>create(GinjectorSingleton.class).getCoreUI();
src/main/java/org/jboss/as/console/client/shared/SubsystemMetaData.java
	- add extension hooks to the end of "groups" initialization:
		SubsystemExtensionProcessor extensionProcessor = GWT.create(SubsystemExtensionProcessor.class);
		extensionProcessor.processExtensions(groups);
org/jboss/as/console/client/domain/runtime/DomainRuntimeNavigation.java
	- integrate subsystem extensions:
		metricPredicates.addAll(SubsystemMetaData.getRuntimeExtensions());
org/jboss/as/console/client/standalone/runtime/StandaloneRuntimeNavigation.java
	- integrate subsystem extensions:
		metricPredicates.addAll(SubsystemMetaData.getRuntimeExtensions());
		
The following files work around GWT compiler bugs that affect DMR deserialization.  These can be removed once patched upstream.
	org/jboss/dmr/client/DataInput.java		
	org/jboss/dmr/client/IEEE754.java		

The following uses the specified comparison column as the baseline:
	org/jboss/as/console/client/shared/runtime/plain/PlainColumnView.java

Added files for extension processing and integration.

