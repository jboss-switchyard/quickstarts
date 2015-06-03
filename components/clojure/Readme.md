# Switchyard Clojure Component
This project provides an implementation.clojure enabling the usage of the Clojure language in a SwitchYard service implementation.

_ _ _

## Using Clojure script "inlined" in SwitchYard
    <sd:switchyard 
        xmlns="urn:switchyard-component-clojure:config:1.0" 
        xmlns:sca="http://docs.oasis-open.org/ns/opencsa/sca/200912" 
        xmlns:sd="urn:switchyard-config:switchyard:1.0"
        xmlns:bean="urn:switchyard-component-bean:config:1.0">
    
        <sca:composite>
        
            <sca:component name="ClojureComponent">
                <sca:service name="OrderService" >
                    <sca:interface.java interface="org.switchyard.component.clojure.deploy.support.OrderService"/>
                </sca:service>
                
                <implementation.clojure>
                    <script>
                        (ns org.switchyard 
                            (:import org.switchyard.Exchange))
                        (defn process [ex] (.setContent (.getMessage ex) "Fletch") (.getContent (.getMessage ex)))
                    </script>
                </implementation.clojure>
            </sca:component>
            
        </sca:composite>

    </sd:switchyard>
    
The Clojure function is passed the SwitchYard Exchange and is required to import that class. It then as access to the complete content of the Exchange to operate on.

Calling this service is done in the same way as calling any other SwitchYard service, for example:

    String title = (String) newInvoker("OrderService").operation("getTitleForItem").sendInOut(10).getContent(String.class);

_ _ _

## Using external Clojure script 
    <sd:switchyard 
        xmlns="urn:switchyard-component-clojure:config:1.0" 
        xmlns:sca="http://docs.oasis-open.org/ns/opencsa/sca/200912" 
        xmlns:sd="urn:switchyard-config:switchyard:1.0"
        xmlns:bean="urn:switchyard-component-bean:config:1.0">
    
        <sca:composite>
        
            <sca:component name="ClojureComponent">
                <sca:service name="OrderService" >
                    <sca:interface.java interface="org.switchyard.component.clojure.deploy.support.OrderService"/>
                </sca:service>
                
                <implementation.clojure scriptFile="clojure.clj"/>
            </sca:component>
            
        </sca:composite>

    </sd:switchyard>
    
The Clojure script can be located on the classpath or in an external file.

_ _ _

## Message content/Exchange injection
The Clojure function can be injected with the content on the SwitchYard Message object or with the complete Exchange. By default, the Message content is injected but you can specify the attribute
_injectExchange_ to inject the complete Exchange. For example:

    <implementation.clojure injectExchange="true" scriptFile="file://sample.clj"/>
    
_ _ _
