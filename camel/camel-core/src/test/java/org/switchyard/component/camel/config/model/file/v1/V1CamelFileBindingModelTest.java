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
package org.switchyard.component.camel.config.model.file.v1;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;

import org.apache.camel.CamelContext;
import org.apache.camel.component.file.FileEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.component.camel.config.model.file.CamelFileBindingModel;
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.Validation;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Daniel Bevenius
 */
public class V1CamelFileBindingModelTest {
    
    @Test
    public void validateCamelBindingModelWithBeanElement() throws Exception {
        final V1CamelFileBindingModel bindingModel = getFirstCamelBinding("switchyard-file-binding-beans.xml");
        final Validation validateModel = bindingModel.validateModel();
        
        assertThat(validateModel.isValid(), is(true));
        assertThat(bindingModel.getTargetDir().toString(), is(equalTo(expectedDirectoryName())));
    }
    
    private String expectedDirectoryName() {
        return "/input/directory";
    }
    
    @Test
    public void testCamelEndpoint()  throws Exception {
        CamelFileBindingModel model = getFirstCamelBinding("switchyard-file-binding-beans.xml");
        String configUri = model.getComponentURI().toString();
        
        CamelContext context = new DefaultCamelContext();
        FileEndpoint endpoint = context.getEndpoint(configUri, FileEndpoint.class);
        Assert.assertEquals(endpoint.getConfiguration().getDirectory(), 
                expectedDirectoryName().replace("/", File.separator));
        Assert.assertEquals(endpoint.getEndpointUri().toString(), "file:///input/directory");
    }
    
    private V1CamelFileBindingModel getFirstCamelBinding(final String config) throws Exception {
        final InputStream in = Classes.getResourceAsStream(config, getClass());
        final SwitchYardModel model = new ModelPuller<SwitchYardModel>().pull(in);
        final List<CompositeServiceModel> services = model.getComposite().getServices();
        final CompositeServiceModel compositeServiceModel = services.get(0);
        final List<BindingModel> bindings = compositeServiceModel.getBindings();
        return (V1CamelFileBindingModel) bindings.get(0);
    }
}
