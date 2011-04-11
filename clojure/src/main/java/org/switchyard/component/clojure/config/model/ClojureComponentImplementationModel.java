/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
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
     * The "clojure" namespace.
     */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-component-clojure:config:1.0";
    
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
