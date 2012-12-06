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
package org.switchyard.component.camel.config.model.mock.v1;

import static org.junit.Assert.assertEquals;

import org.apache.camel.component.mock.MockEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.core.model.mock.v1.V1CamelMockBindingModel;
import org.switchyard.component.camel.core.model.v1.V1CamelBindingModel;

/**
 * Test for {@link V1CamelBindingModel}.
 *
 * @author Mario Antollini
 */
public class V1CamelMockBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelMockBindingModel, MockEndpoint> {

    private static final String CAMEL_XML = "switchyard-mock-binding-beans.xml";

    private static final String NAME = "fooMockName";
    private static final Integer REPORT_GROUP = new Integer(999);

    private static final String CAMEL_URI = "mock://fooMockName?reportGroup=999";

    public V1CamelMockBindingModelTest() {
        super(MockEndpoint.class, CAMEL_XML);
    }

    @Override
    protected V1CamelMockBindingModel createTestModel() {
        return new V1CamelMockBindingModel().setName(NAME).setReportGroup(REPORT_GROUP);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

    @Override
    protected void createModelAssertions(V1CamelMockBindingModel model) {
        assertEquals(NAME, model.getName());
        assertEquals(REPORT_GROUP, model.getReportGroup());
    }

}