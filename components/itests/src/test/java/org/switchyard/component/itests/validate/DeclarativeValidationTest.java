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
package org.switchyard.component.itests.validate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Message;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

/**
 * Functional test for Message Validator.
 * 
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-declarative-validate-test.xml", mixins = CDIMixIn.class)
public class DeclarativeValidationTest  {
    
    @ServiceOperation("ValidationService.testReconsumeStreamXml")
    private Invoker _reconsume;

    private static final String INPUT = "xml/declarative-validate/input.xml";
    private static final QName TYPE = new QName("urn:validate-test:reconsume-stream-xml:1.0", "order");
    @Test
    public void testReconsumeStreamXml() throws Exception {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(INPUT);
        _reconsume.inputType(TYPE);
        _reconsume.expectedOutputType(TYPE);
        Message response = _reconsume.sendInOut(stream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent(InputStream.class)));
        StringBuilder buf = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            buf.append(line);
        }
        reader.close();
        String actual = buf.toString();
        
        stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(INPUT);
        reader = new BufferedReader(new InputStreamReader(stream));
        buf = new StringBuilder();
        while((line = reader.readLine()) != null) {
            buf.append(line);
        }
        reader.close();
        String expected = buf.toString();
        
        Assert.assertEquals(expected, actual);
    }
}

