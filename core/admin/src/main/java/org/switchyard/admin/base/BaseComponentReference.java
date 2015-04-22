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
package org.switchyard.admin.base;

import javax.xml.namespace.QName;

import org.switchyard.admin.ComponentReference;

/**
 * BaseComponentReference
 * 
 * Base implementation for {@link ComponentReference}.
 * 
 * @author Rob Cernich
 */
public class BaseComponentReference extends BaseMessageMetricsAware implements ComponentReference {

    private final QName _name;
    private final String _interface;

    /**
     * Create a new BaseComponentReference.
     * 
     * @param name the name of the reference
     * @param interfaceName the required interface
     */
    public BaseComponentReference(QName name, String interfaceName) {
        _name = name;
        _interface = interfaceName;
    }

    @Override
    public QName getName() {
        return _name;
    }

    @Override
    public String getInterface() {
        return _interface;
    }

}
