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

Added files for extension processing and integration.

