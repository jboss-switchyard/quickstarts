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

package org.switchyard.component.camel.config.model.seda.v1;

import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.camel.config.model.seda.CamelSedaBindingModel;
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.Validation;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;


/**
 * Test for {@link V1CamelBindingModel}.
 *
 * @author Mario Antollini
 */
public class V1CamelSedaBindingModelTest {


    private static final String CAMEL_XML = "switchyard-seda-binding-beans.xml";

    private static final String NAME = "fooSedaName";
    private static final Integer SIZE = new Integer(55);
    private static final Integer CONCURRENT_CONSUMERS = new Integer(3);
    private static final String WAIT_FOR_TASK_TO_COMPLETE = "Always";
    private static final Long TIMEOUT = new Long(1000);
    private static final Boolean MULTIPLE_CONSUMERS = Boolean.TRUE;
    private static final Boolean LIMIT_CONCURRENT_CONSUMERS = Boolean.FALSE;
    
    private static final String CAMEL_URI = "seda:fooSedaName?size=55" +
    		"&waitForTaskToComplete=Always&concurrentConsumers=3" +
    		"&timeout=1000&multipleConsumers=true&limitConcurrentConsumers=false";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testConfigOverride() {
        // Set a value on an existing config element
        CamelSedaBindingModel bindingModel = createSedaModel();
        Assert.assertEquals(NAME, bindingModel.getName());
        bindingModel.setName("newFooSedaName");
        Assert.assertEquals("newFooSedaName", bindingModel.getName());
    }

    @Test
    public void testReadConfig() throws Exception {
        final CamelSedaBindingModel bindingModel = getCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        Assert.assertEquals(validateModel.isValid(), true);
        //Camel Seda
        Assert.assertEquals(bindingModel.getName(), NAME);
        Assert.assertEquals(bindingModel.getSize(), SIZE);
        Assert.assertEquals(bindingModel.getConcurrentConsumers(), CONCURRENT_CONSUMERS);
        Assert.assertEquals(bindingModel.getWaitForTaskToComplete(), WAIT_FOR_TASK_TO_COMPLETE);
        Assert.assertEquals(bindingModel.getTimeout(), TIMEOUT);
        Assert.assertEquals(bindingModel.isMultipleConsumers(), MULTIPLE_CONSUMERS);
        Assert.assertEquals(bindingModel.isLimitConcurrentConsumers(), LIMIT_CONCURRENT_CONSUMERS);
        //URI
        Assert.assertEquals(bindingModel.getComponentURI().toString(), CAMEL_URI);
    }

    @Test
    public void testWriteConfig() throws Exception {
        CamelSedaBindingModel bindingModel = createSedaModel();
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        Assert.assertEquals(validateModel.isValid(), true);
        //Camel Seda
        Assert.assertEquals(bindingModel.getName(), NAME);
        Assert.assertEquals(bindingModel.getSize(), SIZE);
        Assert.assertEquals(bindingModel.getConcurrentConsumers(), CONCURRENT_CONSUMERS);
        Assert.assertEquals(bindingModel.getWaitForTaskToComplete(), WAIT_FOR_TASK_TO_COMPLETE);
        Assert.assertEquals(bindingModel.getTimeout(), TIMEOUT);
        Assert.assertEquals(bindingModel.isMultipleConsumers(), MULTIPLE_CONSUMERS);
        Assert.assertEquals(bindingModel.isLimitConcurrentConsumers(), LIMIT_CONCURRENT_CONSUMERS);
        //URI
        Assert.assertEquals(bindingModel.getComponentURI().toString(), CAMEL_URI);
    }

    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getCamelBinding(CAMEL_XML).toString();
        String newXml = createSedaModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        Assert.assertTrue(diff.toString(), diff.similar());
    }

    private CamelSedaBindingModel createSedaModel() {
        return new V1CamelSedaBindingModel().setName(NAME)
        .setSize(SIZE)
        .setConcurrentConsumers(CONCURRENT_CONSUMERS)
        .setWaitForTaskToComplete(WAIT_FOR_TASK_TO_COMPLETE)
        .setTimeout(TIMEOUT)
        .setMultipleConsumers(MULTIPLE_CONSUMERS)
        .setLimitConcurrentConsumers(LIMIT_CONCURRENT_CONSUMERS);
    }


    private CamelSedaBindingModel getCamelBinding(final String config) throws Exception {
        final InputStream in = getClass().getResourceAsStream(config);
        final SwitchYardModel model = (SwitchYardModel) new ModelResource<SwitchYardModel>().pull(in);
        final List<CompositeServiceModel> services = model.getComposite().getServices();
        final CompositeServiceModel compositeServiceModel = services.get(0);
        final List<BindingModel> bindings = compositeServiceModel.getBindings();
        return (CamelSedaBindingModel) bindings.get(0);
    }

}