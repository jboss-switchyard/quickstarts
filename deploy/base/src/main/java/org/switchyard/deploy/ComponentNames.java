/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
