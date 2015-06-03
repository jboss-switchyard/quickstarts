/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.clojure.config.model;

import org.switchyard.config.model.composite.ComponentImplementationModel;

/**
 * A definition of an 'implementation.clojure' element.
 * 
 * @author Daniel Bevenius
 *
 */
public interface ClojureComponentImplementationModel extends ComponentImplementationModel {
    
    /**
     * The 'clojure' implementation type.
     */
    String CLOJURE = "clojure";
    
    /**
     * The 'script' element name.
     */
    String SCRIPT = "script";
    
    /**
     * The 'scriptFile' attribute name.
     */
    String SCRIPT_FILE = "scriptFile";
    
    /**
     * The 'injectMessageOnly' attribute name.
     */
    String INJECT_EXCHANGE = "injectExchange";
    
    /**
     * Gets the clojure script that is the actual Clojure code.
     * @return {@link ClojureScriptModel} the clojure script configuration model.
     */
    ClojureScriptModel getScriptModel();
    
    /**
     * Sets the clojure script model.
     * @param scriptModel The script configuration model.
     * @return {@link ClojureComponentImplementationModel} to enable method chaining.
     */
    ClojureComponentImplementationModel setScriptModel(final ClojureScriptModel scriptModel);
    
    /**
     * Gets the clojure script that is the actual Clojure code.
     * @return String the clojure script to execute.
     */
    String getScriptFile();
    
    /**
     * Sets the scriptFile.
     * 
     * @param scriptFile The script file.
     * @return {@link ClojureComponentImplementationModel} to enable method chaining.
     */
    ClojureComponentImplementationModel setScriptFile(final String scriptFile);
    
    /**
     * Determines whether the complete Exchange should be injected into the Clojure script.
     * 
     * @return true If the Exchange should be injected.
     */
    Boolean injectExchange();
    
    /**
     * Sets the 'injectExchange' property whether the complete Exchange should be injected into the Clojure script.
     * 
     * @param enable The value to 'injectExchange to.
     * @return {@link ClojureComponentImplementationModel} to enable method chaining.
     */
    ClojureComponentImplementationModel setInjectExchange(final Boolean enable);
    

}
