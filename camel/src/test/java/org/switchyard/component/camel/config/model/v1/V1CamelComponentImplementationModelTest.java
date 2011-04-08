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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStream;

import org.apache.camel.model.RouteDefinition;
import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.Validation;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Daniel Bevenius
 */
public class V1CamelComponentImplementationModelTest {
    
    @Test
    public void validateCamelImplementationModelWithBeanElement() throws Exception {
        final V1CamelImplementationModel implModel = getFirstCamelBinding("switchyard-implementation-beans.xml");
        final Validation validateModel = implModel.validateModel();
        
        assertThat(validateModel.isValid(), is(true));
        
        
        final RouteDefinition route = implModel.getRoute();
        assertThat(route, is(notNullValue()));
    }
    
    private V1CamelImplementationModel getFirstCamelBinding(final String config) throws Exception {
        final InputStream in = Classes.getResourceAsStream(config, getClass());
        final SwitchYardModel model = new ModelResource<SwitchYardModel>().pull(in);
        final ComponentModel componentModel = model.getComposite().getComponents().get(1);
        final ComponentImplementationModel implementation = componentModel.getImplementation();
        return (V1CamelImplementationModel) implementation;
    }
}
