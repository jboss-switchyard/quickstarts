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
package org.switchyard.component.camel.jpa.model.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.jpa.JpaEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;

/**
 * Test for {@link V1CamelJpaBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelJpaBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelJpaBindingModel, JpaEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-jpa-binding-beans.xml";
    private static final String CAMEL_URI = "jpa://some.clazz.Name?persistenceUnit=MyPU";
    static final String ENTITY_CLASS_NAME = "some.clazz.Name";
    static final String PERSISTENCE_UNIT = "MyPU";

    public V1CamelJpaBindingModelTest() {
        super(JpaEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V1CamelJpaBindingModel createTestModel() {
        return new V1CamelJpaBindingModel()
            .setEntityClassName(ENTITY_CLASS_NAME)
            .setPersistenceUnit(PERSISTENCE_UNIT);
    }

    @Override
    protected void createModelAssertions(V1CamelJpaBindingModel model) {
        assertEquals(ENTITY_CLASS_NAME, model.getEntityClassName());
        assertEquals(PERSISTENCE_UNIT, model.getPersistenceUnit());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
