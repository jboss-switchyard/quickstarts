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
package org.switchyard.component.camel.config.model.v1;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.apache.camel.component.direct.DirectEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelReferenceBindingModelTest;
import org.switchyard.component.camel.core.model.v1.V1CamelBindingModel;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Daniel Bevenius
 */
public class V1CamelReferenceBindingModelTest extends V1BaseCamelReferenceBindingModelTest<V1CamelBindingModel, DirectEndpoint> {

    private final static String CAMEL_XML = "switchyard-camel-ref-beans.xml";
    private final static String CAMEL_URI = "direct://input";

    public V1CamelReferenceBindingModelTest() {
        super(DirectEndpoint.class, CAMEL_XML);
    }

    @Override
    protected V1CamelBindingModel createTestModel() {
        return new V1CamelBindingModel()
            .setConfigURI(URI.create("direct://input"));
    }

    @Override
    protected void createModelAssertions(V1CamelBindingModel model) {
        assertEquals("uri", model.getType());
        assertEquals("direct", model.getConfigURI().getScheme());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
