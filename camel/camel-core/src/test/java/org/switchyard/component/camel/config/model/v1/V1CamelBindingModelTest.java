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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.Validation;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Daniel Bevenius
 */
public class V1CamelBindingModelTest {
    
    @Test
    public void validateCamelBindingModelWithBeanElement() throws Exception {
        final V1CamelBindingModel bindingModel = getCamelBindingFromCompositeService("switchyard-camel-binding-beans.xml");
        final Validation validateModel = bindingModel.validateModel();
        
        assertThat(validateModel.isValid(), is(true));
        assertThat(bindingModel.getType(), is("camel"));
        assertThat(bindingModel.getConfigURI().getScheme(), is("direct"));
        assertThat(bindingModel.getOperationSelector().getOperationName(), is("print"));
    }
    
    private V1CamelBindingModel getCamelBindingFromCompositeService(final String config) throws Exception {
        final List<CompositeServiceModel> services = getSwitchYardModel(config).getComposite().getServices();
        final CompositeServiceModel compositeServiceModel = services.get(0);
        return (V1CamelBindingModel) compositeServiceModel.getBindings().get(0);
    }
    
    private SwitchYardModel getSwitchYardModel(final String config) throws IOException {
        return new ModelPuller<SwitchYardModel>().pull(config, getClass());
    }
    
    @Test
    public void validateCamelBindingModelWithReference() throws Exception {
        final BindingModel bindingModel = getCamelBindingFromCompositeReference("switchyard-camel-ref-beans.xml");
        final Validation validateModel = bindingModel.validateModel();
        
        assertThat(validateModel.isValid(), is(true));
        assertThat(bindingModel, is(instanceOf(V1CamelBindingModel.class)));
    }
    
    private BindingModel getCamelBindingFromCompositeReference(final String config) throws Exception {
        final List<CompositeReferenceModel> references = getSwitchYardModel(config).getComposite().getReferences();
        return references.get(0).getBindings().get(0);
    }
}
