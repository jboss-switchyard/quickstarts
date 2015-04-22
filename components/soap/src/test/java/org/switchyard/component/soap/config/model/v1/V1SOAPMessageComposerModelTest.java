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
package org.switchyard.component.soap.config.model.v1;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.config.model.SOAPNamespace;
import org.switchyard.config.model.ModelPuller;

/**
 * Test of atom binding model.
 */
public class V1SOAPMessageComposerModelTest {

    private static final String SOAP_BINDING = "soap-binding.xml";
    private static final String COMPOSER_FRAG = "message-composer.xml";
    
    @Test
    public void testReadConfigFragment() throws Exception {
        ModelPuller<V1SOAPMessageComposerModel> puller = new ModelPuller<V1SOAPMessageComposerModel>();
        V1SOAPMessageComposerModel model = puller.pull(COMPOSER_FRAG, getClass());
        Assert.assertTrue("Unwrap should be true", model.isUnwrapped());
    }
    
    @Test
    public void testReadConfigBinding() throws Exception {
        ModelPuller<SOAPBindingModel> puller = new ModelPuller<SOAPBindingModel>();
        SOAPBindingModel model = puller.pull(SOAP_BINDING, getClass());
        Assert.assertTrue(model.isModelValid());
        Assert.assertTrue("Unwrap should be true", model.getSOAPMessageComposer().isUnwrapped());
    }

    @Test
    public void testWriteConfig() throws Exception {
        V1SOAPMessageComposerModel scm = new V1SOAPMessageComposerModel(SOAPNamespace.V_1_0.uri());
        scm.setUnwrapped(true);

        V1SOAPMessageComposerModel refModel = new ModelPuller<V1SOAPMessageComposerModel>()
                .pull(COMPOSER_FRAG, getClass());
        
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refModel.toString(), scm.toString());
        Assert.assertTrue(diff.toString(), diff.similar());
    }
}
