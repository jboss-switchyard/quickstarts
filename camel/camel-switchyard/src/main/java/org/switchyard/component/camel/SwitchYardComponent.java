/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *  *
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

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

/**
 * SwitchYardComponent enable Switchyard services to be exposed through Apache Camel.
 * <p/>
 * This classes {@link #createEndpoint(String, String, Map)} creates a {@link SwitchYardEndpoint}.
 * 
 * Example usage using Camel's Java DSL:
 * <pre>
 * from("switchyard://someIncomingService")
 * ...
 * .to("switchyard://mySwitchyardService")
 * </pre>
 *
 * @author Daniel Bevenius
 */
public class SwitchYardComponent extends DefaultComponent {

    /**
     * Property added to each Camel Context so that code initialized inside 
     * Camel can access the SY service domain.
     */
    public static final String SERVICE_DOMAIN = "org.switchyard.camel.serviceDomain";

    @Override
    protected Endpoint createEndpoint(final String uri, final String path, final Map<String, Object> parameters) throws Exception {
        final String namespace = (String) parameters.remove("namespace");
        final String operationName = (String) parameters.remove("operationName");
        return new SwitchYardEndpoint(uri, this, namespace, operationName);
    }

}
