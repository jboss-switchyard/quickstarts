# Switchyard Forge Clojure Component
This project provides an implementation of a Forge plugin capable of generating Clojure services in SwitchYard.
_ _ _

## Installing the Clojure Facet
Before you start make sure that you have the required plugins in your Forge classpath. This can be done by putting the jars in Forge's _lib_ directory or
putting them in _~/.forge/plugins_. 

The Clojure plugin depend on the switchyard faced. The SwitchYard facet will update the current project and add files like _switchyard.xml_.
To see which facets are install you can run the following command:
    > project list-facets
    
The facets that are green in this list are the ones that are installed on the current project.

### Installing the _switchyard_ facet
    > project install-facet switchyard

### Installing the _switchyard.clojure_ facet
    > project install-facet switchyard.clojure
    
_ _ _
## clojure-service create command
    > help clojure-service create

    [clojure-service create] - Create a new implemenation.clojure

    [OPTIONS]
        [--serviceName, -s] - The service name - The SwitchYard service name to use for this implementation
        [--inlineScript, -i] - Use inline Clojure script - Path to the Clojure script to inline, the content will be placed into the script element
        [--emptyInlineScript] - Creates an empty 'script' element - An empty 'script' element will be created that can be filled in later.
        [--externalScriptPath, -e] - Path to the external Clojure Script - Path to the external Clojure script to be referenced from the 'scriptFile' attribute
        [--emptyExternalScriptPath] - Creates an empty 'scriptFile' attribute - An empty 'scriptFile' attribute will be created that can be filled in later.
        [--injectExchange, -x] - Inject the SwitchYard Exchange object into the Clojure script - The SwitchYard Exchange will be injected into the Clojure script if this value is set. If not, only the Message content will be injected.

### Creating with an _inline_ script
    > clojure-service create --serviceName testing --inlineScript sample.clj --injectExchange 

### Creating with an external script
    > clojure-service create --scriptFile "/some/path/sample.clj"
_ _ _

## Enable stacktraces in Forge
	set VERBOSE true
_ _ _
