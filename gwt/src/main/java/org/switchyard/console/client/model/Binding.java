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
package org.switchyard.console.client.model;

/**
 * Binding
 * 
 * Represents a gateway binding on a service.
 * 
 * @author Rob Cernich
 */
public interface Binding {

    /**
     * @return the name of binding (e.g. soap1)
     */
    public String getName();

    /**
     * @param name the name of binding (e.g. soap1)
     */
    public void setName(String name);

    /**
     * @return the type of binding (e.g. soap)
     */
    public String getType();

    /**
     * @param type the type of binding (e.g. soap)
     */
    public void setType(String type);

    /**
     * @return the raw configuration of the binding
     */
    public String getConfiguration();

    /**
     * @param configuration the raw configuration of the binding.
     */
    public void setConfiguration(String configuration);

    /**
     * @return the state of the binding.
     */
    public State getState();

    /**
     * @param state the state of the binding.
     */
    public void setState(State state);
}
