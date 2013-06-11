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
package org.switchyard.component.camel.config.model.direct.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.direct.DirectEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.core.model.direct.v1.V1CamelDirectBindingModel;
import org.switchyard.component.camel.core.model.v1.V1CamelBindingModel;

/**
 * Test for {@link V1CamelBindingModel}.
 *
 * @author Mario Antollini
 */
public class V1CamelDirectBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelDirectBindingModel, DirectEndpoint> {

    private static final String CAMEL_XML = "switchyard-direct-binding-beans.xml";

    private static final String NAME = "fooDirectName";

    private static final String CAMEL_URI = "direct://fooDirectName";

    public V1CamelDirectBindingModelTest() {
        super(DirectEndpoint.class, CAMEL_XML);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

    @Override
    protected void createModelAssertions(V1CamelDirectBindingModel model) {
        assertEquals(NAME, model.getEndpointName());
    }

    @Override
    protected V1CamelDirectBindingModel createTestModel() {
        return new V1CamelDirectBindingModel().setEndpointName(NAME);
    }

}