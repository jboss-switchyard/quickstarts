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
package org.switchyard.component.camel.deploy;

import java.net.URI;

import javax.xml.namespace.QName;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.camel.SwitchYardRouteDefinition;

/**
 * Utility class that takes care of creating Camel component uris for 
 * SwitchYard services.
 * 
 * @author Daniel Bevenius
 *
 */
public final class ComponentNameComposer {
    /**
     *Component name used for endpoint URIs.
     */
    public static final String SWITCHYARD_COMPONENT_NAME = "switchyard";
    
    private ComponentNameComposer() {
    }
    
    /**
     * Creates a Camel endpoint URI based on the component and service name.  
     * Returns a String that has the format:
     * <pre>
     * {@value #SWITCHYARD_COMPONENT_NAME}://serviceName.getLocalPart()
     * </pre>
     * @param serviceName SwitchYard service name
     * @return camel endpoint URI based on component and service name
     */
    public static String composeComponentUri(final QName serviceName) {
        final StringBuilder sb = new StringBuilder();
        sb.append(SWITCHYARD_COMPONENT_NAME).append("://").append(serviceName.getLocalPart());
        return SwitchYardRouteDefinition.addNamespaceParameter(sb.toString(), serviceName.getNamespaceURI());
    }
    
    /**
     * Composes a SwitchYard service name, a QName, from the passed-in string uri.
     * 
     * @param namespace the service namespace
     * @param uri a string uri.
     * @return QName a SwitchYard service name
     */
    public static QName composeSwitchYardServiceName(final String namespace, final String uri) {
        final URI create = URI.create(uri);
        final String path = create.getAuthority();
        return XMLHelper.createQName(namespace, path);
    }
    
}
