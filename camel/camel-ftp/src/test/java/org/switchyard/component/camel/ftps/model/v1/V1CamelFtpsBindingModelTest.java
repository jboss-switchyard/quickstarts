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
package org.switchyard.component.camel.ftps.model.v1;

import static org.junit.Assert.assertEquals;

import org.apache.camel.component.file.remote.FtpsEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;

/**
 * Test for {@link V1CamelFtpsBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelFtpsBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelFtpsBindingModel, FtpsEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-ftps-binding-beans.xml";
    private static final String DIRECTORY = "test";
    private static final String HOST = "localhost";
    private static final Boolean IMPLICT = false;
    private static final String CAMEL_URI = "ftps://localhost/test?isImplicit=false";

    public V1CamelFtpsBindingModelTest() {
        super(FtpsEndpoint.class, CAMEL_XML);
    }

    @Override
    protected V1CamelFtpsBindingModel createTestModel() {
        V1CamelFtpsBindingModel model = (V1CamelFtpsBindingModel) new V1CamelFtpsBindingModel()
            .setDirectory(DIRECTORY)
            .setHost(HOST);

        return model.setImplict(IMPLICT);
    }

    @Override
    protected void createModelAssertions(V1CamelFtpsBindingModel model) {
        assertEquals(DIRECTORY, model.getDirectory());
        assertEquals(HOST, model.getHost());
        assertEquals(IMPLICT, model.isImplict());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
