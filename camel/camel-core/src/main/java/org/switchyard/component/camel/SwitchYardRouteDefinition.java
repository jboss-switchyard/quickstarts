/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.reflect.MethodAccess;
import org.switchyard.exception.SwitchYardException;

/**
 * Adds capability of appending a namespace on the end of switchyard:// "from" and "to" URIs.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class SwitchYardRouteDefinition extends RouteDefinition {

    private final String _namespace;

    /**
     * Constructs a new SwitchYardRouteDefinition with the specified namespace.
     * @param namespace the specified namespace
     */
    public SwitchYardRouteDefinition(String namespace) {
        super();
        _namespace = namespace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RouteDefinition from(String uri) {
        uri = addNamespaceParameter(uri, _namespace);
        return super.from(uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RouteDefinition to(String uri) {
        uri = addNamespaceParameter(uri, _namespace);
        return super.to(uri);
    }

    /**
     * Appends a namespace on the end of the specified uri.
     * @param uri the specified uri
     * @param namespace the specified namespace
     * @return the appended uri
     */
    public static final String addNamespaceParameter(String uri, String namespace) {
        if (uri != null && uri.startsWith("switchyard://")) {
            namespace = Strings.trimToNull(namespace);
            if (namespace != null) {
                if (!uri.contains("?namespace=") && !uri.contains("&namespace=")) {
                    try {
                        namespace = URLEncoder.encode(namespace, "UTF-8");
                    } catch (UnsupportedEncodingException uee) {
                        throw new SwitchYardException(uee);
                    }
                    StringBuilder sb = new StringBuilder(uri);
                    if (uri.indexOf('?') < 0) {
                        sb.append('?');
                    } else {
                        sb.append('&');
                    }
                    sb.append("namespace=");
                    sb.append(namespace);
                    uri = sb.toString();
                }
            }
        }
        return uri;
    }

    /**
     * Appends a namespace on the end of all URIs found in the specified route.
     * @param route the specified route
     * @param namespace the specified namespace
     */
    public static final void addNamespaceParameter(RouteDefinition route, String namespace) {
        for (FromDefinition fromDef : route.getInputs()) {
            addNamespaceParameterFrom(fromDef, namespace);
        }
        for (ProcessorDefinition<?> procDef : route.getOutputs()) {
            addNamespaceParameterTo(procDef, namespace);
        }
    }

    private static final void addNamespaceParameterFrom(FromDefinition fromDef, String namespace) {
        String old_uri = fromDef.getUri();
        String new_uri = addNamespaceParameter(old_uri, namespace);
        fromDef.setUri(new_uri);
        setEndpointUri(fromDef.getEndpoint(), namespace);
    }

    private static final void addNamespaceParameterTo(ProcessorDefinition<?> procDef, String namespace) {
        if (procDef instanceof ToDefinition) {
            ToDefinition toDef = (ToDefinition)procDef;
            String old_uri = toDef.getUri();
            String new_uri = addNamespaceParameter(old_uri, namespace);
            toDef.setUri(new_uri);
            setEndpointUri(toDef.getEndpoint(), namespace);
        }
        for (ProcessorDefinition<?> procDefChild : procDef.getOutputs()) {
            addNamespaceParameterTo(procDefChild, namespace);
        }
    }

    private static final void setEndpointUri(Endpoint endpoint, String namespace) {
        if (endpoint instanceof DefaultEndpoint) {
            String old_uri = endpoint.getEndpointUri();
            String new_uri = addNamespaceParameter(old_uri, namespace);
            if (!old_uri.equals(new_uri)) {
                try {
                    Method setEndpointUri = DefaultEndpoint.class.getDeclaredMethod("setEndpointUri", new Class[]{String.class});
                    new MethodAccess<String>(null, setEndpointUri).write((DefaultEndpoint)endpoint, new_uri);
                } catch (NoSuchMethodException nsfe) {
                    throw new SwitchYardException(nsfe);
                }
            }
        }
    }

}
