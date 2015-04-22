/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.config.model.property;

import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * SwitchYardModelTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class PropertiesModelTests {

    private static final String XML = "/org/switchyard/config/model/property/PropertiesModelTests.xml";

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }

    @Test
    public void testRead() throws Exception {
        SwitchYardModel switchyard = _puller.pull(XML, getClass());
        PropertiesModel propsModel = switchyard.getDomain().getProperties();
        Assert.assertEquals(2, propsModel.getProperties().size()); // only includes (child) property models, not loaded properties
        Properties propsProps = propsModel.toProperties();
        Assert.assertEquals(3, propsProps.size()); // combined properties contains all properties
        Map<String, String> propsMap = propsModel.toMap();
        Assert.assertEquals(3, propsMap.size()); // combined map contains all properties
        Assert.assertEquals("tiger", propsModel.getPropertyValue("cat")); // property model overrides loaded properties
        Assert.assertEquals("tiger", propsModel.resolveProperty("cat"));
        Assert.assertEquals("tiger", propsProps.getProperty("cat"));
        Assert.assertEquals("tiger", propsMap.get("cat"));
        Assert.assertEquals("vizsla", propsModel.getPropertyValue("dog")); // only exists in property model, combined properties, and map
        Assert.assertEquals("vizsla", propsModel.resolveProperty("dog"));
        Assert.assertEquals("vizsla", propsProps.getProperty("dog"));
        Assert.assertEquals("vizsla", propsMap.get("dog"));
        Assert.assertNull(propsModel.getProperty("bird")); // only exists in loaded properties, combined properties, and map
        Assert.assertEquals("crow", propsModel.getPropertyValue("bird"));
        Assert.assertEquals("crow", propsModel.resolveProperty("bird"));
        Assert.assertEquals("crow", propsProps.getProperty("bird"));
        Assert.assertEquals("crow", propsMap.get("bird"));
    }

    @Test
    public void testWrite() throws Exception {
        String old_xml = new StringPuller().pull(XML, getClass());
        SwitchYardModel switchyard = _puller.pull(new StringReader(old_xml));
        String new_xml = switchyard.toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testValidate() throws Exception {
        SwitchYardModel switchyard = _puller.pull(XML, getClass());
        switchyard.assertModelValid();
    }

}
