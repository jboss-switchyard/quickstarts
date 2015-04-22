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
package org.switchyard.deploy;

import javax.xml.namespace.QName;

import org.switchyard.ServiceReference;

/**
 * Utility class for constructing and parsing service and reference names 
 * qualified with a service component name.
 */
public final class ComponentNames {
    
    private ComponentNames() {
        
    }

    /**
     * Construct a qualified reference name based on the specified component and reference name.
     * @param componentName service component name
     * @param referenceName reference name
     * @return qualified name
     */
    public static QName qualify(QName componentName, QName referenceName) {
        return qualify(componentName.getLocalPart(), 
                referenceName.getLocalPart(), componentName.getNamespaceURI());
    }

    /**
     * Construct a qualified reference name based on the specified component, reference name
     * and namespace.
     * @param componentName service component name
     * @param referenceName reference name
     * @param namespaceURI namespace URI
     * @return qualified name
     */
    public static QName qualify(String componentName, String referenceName, String namespaceURI) {
        return new QName(namespaceURI, componentName + "/" + referenceName);
    }
    
    /**
     * Return an unqualified name from the given service reference, removing the name of the 
     * service component.
     * @param reference service reference
     * @return name without service component name included
     */
    public static QName unqualify(ServiceReference reference) {
        return unqualify(reference.getName());
    }
    
    /**
     * Return an unqualified name from the given service reference name, removing the name of the 
     * service component.
     * @param refName service reference name
     * @return name without service component name included
     */
    public static QName unqualify(QName refName) {
        if (refName.getLocalPart().contains("/")) {
            String name = refName.getLocalPart().split("/")[1];
            refName = new QName(refName.getNamespaceURI(), name);
        }
        return refName;
    }
}
