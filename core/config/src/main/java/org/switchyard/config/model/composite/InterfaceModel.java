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
package org.switchyard.config.model.composite;

import org.switchyard.config.model.TypedModel;

/**
 * An "interface" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface InterfaceModel extends TypedModel {

    /** The "interface" name. */
    public static final String INTERFACE = "interface";

    /**
     * Name of standard SCA Java interface.
     */
    public static final String JAVA = "java";
    /**
     * Name of standard SCA WSDL interface.
     */
    public static final String WSDL = "wsdl";


    /**
     * Gets the name of the interface.
     * @return the name of the interface
     */
    public String getInterface();

    /**
     * Sets the name of the interface.
     * @param interfaze the name of the interface
     * @return this InterfaceModel (useful for chaining)
     */
    public InterfaceModel setInterface(String interfaze);

}
