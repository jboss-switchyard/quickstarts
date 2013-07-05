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

package org.switchyard.quickstarts.soap.attachment;

import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.common.type.Classes;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {CDIMixIn.class},
        scanners = {BeanSwitchYardScanner.class, TransformSwitchYardScanner.class })
public class SoapAttachmentTest {

    @Test
    public void testSwitchYardWebService() throws Exception {
        SOAPMessage response = SoapAttachmentClient.sendMessage();
        Assert.assertTrue(response.getAttachments().hasNext());
    }
}
