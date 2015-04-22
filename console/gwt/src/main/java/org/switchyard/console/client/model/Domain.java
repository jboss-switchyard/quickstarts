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

import java.util.List;

/**
 * Domain
 * 
 * Represents a SwitchYard configuration domain.
 * 
 * @author Rob Cernich
 */
public interface Domain {
    /**
     * @return the domain's name.
     */
    public String getName();

    /**
     * @return the transformers definined in this domain.
     */
    public List<Transformer> getTransformers();

    /**
     * @return the properties defined in this domain.
     */
    public List<Property> getProperties();
}
