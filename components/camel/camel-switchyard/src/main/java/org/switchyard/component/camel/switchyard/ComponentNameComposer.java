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
package org.switchyard.component.camel.switchyard;

import static org.switchyard.component.camel.common.CamelConstants.SWITCHYARD_COMPONENT_NAME;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.deploy.ComponentNames;

/**
 * Utility class that takes care of creating Camel component uris for 
 * SwitchYard services.
 * 
 * @author Daniel Bevenius
 */
public final class ComponentNameComposer {

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
        return sb.toString();
    }

    /**
     * Composes a SwitchYard service name, a QName, from the passed-in string uri.
     * 
     * @param namespace the service namespace
     * @param uri a string uri.
     * @param componentName service componentName
     * @return QName a SwitchYard service name
     */
    public static QName composeSwitchYardServiceName(
            final String namespace, final String uri, final QName componentName) {
        final URI create = URI.create(uri);
        final String path = create.getAuthority();
        if (componentName != null) {
            return ComponentNames.qualify(componentName.getLocalPart(), path, namespace);
        } else {
            return XMLHelper.createQName(namespace, path);
        }
        
    }

    /**
     * Parses the passed-in URI's query for parameters and returns the as
     * a Map.
     * 
     * @param uri The URI to parse
     * @return Map Containing the query parameters or an empty map if there were no query parameters.
     */
    public static Map<String, String> getQueryParamMap(final URI uri) {
        final Map<String, String> map = new HashMap<String, String>();
        for (final String param : uri.getQuery().split("&")) {
            final String[] nameValue = param.split("=");
            if (nameValue.length == 2) {
                map.put(nameValue[0],  nameValue[1]);
            }
        }
        return map;
    }

}
