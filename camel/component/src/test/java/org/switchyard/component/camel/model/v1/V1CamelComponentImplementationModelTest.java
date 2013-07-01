/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.camel.model.v1;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.component.camel.core.model.v1.V1CamelBindingModel;
import org.switchyard.component.camel.model.CamelComponentImplementationModel;
import org.switchyard.component.camel.scanner.SingleRouteService;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Daniel Bevenius
 */
public class V1CamelComponentImplementationModelTest {
    
    private static boolean oldIgnoreWhitespace;
    
    private static final String XML_ROUTE_PATH = "org/switchyard/component/camel/model/v1/SingleRouteService.xml";

    @BeforeClass
    public static void setup() {
        oldIgnoreWhitespace = XMLUnit.getIgnoreWhitespace();
        XMLUnit.setIgnoreWhitespace(true);
    }

    @AfterClass
    public static void cleanup() {
        XMLUnit.setIgnoreWhitespace(oldIgnoreWhitespace);
    }

    @Test
    public void programmaticConfig() {
        assertThat(createModel().getJavaClass(), is(equalTo(SingleRouteService.class.getName())));
    }

    @Test
    public void validateModelWithJavaElement() throws Exception {
        final V1CamelImplementationModel implModel = getCamelImplementation("switchyard-implementation-java.xml");

        validateModel(implModel);
        assertThat(SingleRouteService.class.getName(), is(equalTo(implModel.getJavaClass())));
    }

    @Test
    public void validateModelWithXMLElement() throws Exception {
        final V1CamelImplementationModel implModel = getCamelImplementation("switchyard-implementation-xml.xml");
        
        validateModel(implModel);
        assertThat(XML_ROUTE_PATH, is(equalTo(implModel.getXMLPath())));
    }

    @Test
    public void addXMLPath() throws Exception {
        V1CamelImplementationModel camelConfig = new V1CamelImplementationModel();
        camelConfig.setXMLPath(XML_ROUTE_PATH);
        validateModel(camelConfig);
        Assert.assertEquals(XML_ROUTE_PATH, camelConfig.getXMLPath());
    }

    @Test
    public void writeConfig() throws Exception {
        final String control = getCamelImplementation("switchyard-implementation-java.xml").toString();
        final String test = createModel().toString();
        XMLAssert.assertXMLEqual(control, test);
    }

    private V1CamelImplementationModel createModel() {
        return new V1CamelImplementationModel().setJavaClass(SingleRouteService.class.getName());
    }

    private void validateModel(final CamelComponentImplementationModel model) {
        assertThat(model.validateModel().isValid(), is(true));
    }

    private V1CamelImplementationModel getCamelImplementation(final String config) throws Exception {
        V1CamelImplementationModel implementation = null;
        final SwitchYardModel model = new ModelPuller<SwitchYardModel>().pull(config, getClass());
        for (ComponentModel componentModel : model.getComposite().getComponents()) {
            if (CamelComponentImplementationModel.CAMEL.equals(componentModel.getImplementation().getType())) {
                implementation = (V1CamelImplementationModel) componentModel.getImplementation();
                break;
            }
        }
        return implementation;
    }
}
