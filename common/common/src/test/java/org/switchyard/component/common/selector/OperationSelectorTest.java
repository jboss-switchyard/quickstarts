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
package org.switchyard.component.common.selector;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.switchyard.component.common.selector.BaseOperationSelector;
import org.switchyard.config.model.selector.OperationSelectorModel;
import org.switchyard.config.model.selector.RegexOperationSelectorModel;
import org.switchyard.config.model.selector.StaticOperationSelectorModel;
import org.switchyard.config.model.selector.XPathOperationSelectorModel;
import org.switchyard.config.model.selector.v1.V1RegexOperationSelectorModel;
import org.switchyard.config.model.selector.v1.V1StaticOperationSelectorModel;
import org.switchyard.config.model.selector.v1.V1XPathOperationSelectorModel;
import org.switchyard.selector.OperationSelector;

public class OperationSelectorTest {

    @Test
    public void testStatic() throws Exception {
        String namespace = "urn:operation-selector-test:1.0";
        String namespace2 = "urn:operation-selector-test:2.0";
        String operation = "invoke";
        String fullname = "{" + namespace2 + "}" + operation;

        StaticOperationSelectorModel model = new V1StaticOperationSelectorModel();
        model.setOperationName(operation);
        OperationSelector<String> selector = new MyOperationSelector(model);
        
        QName operationQName = selector.selectOperation(null);
        Assert.assertEquals(XMLConstants.NULL_NS_URI, operationQName.getNamespaceURI());
        Assert.assertEquals(operation, operationQName.getLocalPart());
        
        selector.setDefaultNamespace(namespace);
        operationQName = selector.selectOperation(null);
        Assert.assertEquals(namespace, operationQName.getNamespaceURI());
        Assert.assertEquals(operation, operationQName.getLocalPart());
        
        model.setOperationName(fullname);
        selector = new MyOperationSelector(model);
        selector.setDefaultNamespace(namespace);
        operationQName = selector.selectOperation(null);
        Assert.assertEquals(namespace2, operationQName.getNamespaceURI());
        Assert.assertEquals(operation, operationQName.getLocalPart());
        
    }
    
    @Test
    public void testXPath() throws Exception {
        String expression = "/Message/Operation";
        String expressionAttribute = "/Message/Operation/@name";
        String content = "<Message><Operation name=\"xpathOperationFromAttribute\">xpathOperation</Operation></Message>";

        XPathOperationSelectorModel model = new V1XPathOperationSelectorModel();
        model.setExpression(expression);
        OperationSelector<String> selector = new MyOperationSelector(model);
        QName operationQName = selector.selectOperation(content);
        Assert.assertEquals("xpathOperation", operationQName.getLocalPart());
        
        model.setExpression(expressionAttribute);
        selector = new MyOperationSelector(model);
        operationQName = selector.selectOperation(content);
        Assert.assertEquals("xpathOperationFromAttribute", operationQName.getLocalPart());
    }
    
    @Test
    public void testRegex() throws Exception {
        String expression = "[a-zA-Z]*Operation";
        String content = "xxx yyy zzz regexOperation aaa bbb ccc";

        RegexOperationSelectorModel model = new V1RegexOperationSelectorModel();
        model.setExpression(expression);
        OperationSelector<String> selector = new MyOperationSelector(model);
        QName operationQName = selector.selectOperation(content);
        Assert.assertEquals("regexOperation", operationQName.getLocalPart());
    }
    
    public class MyOperationSelector extends BaseOperationSelector<String> {
        public MyOperationSelector(OperationSelectorModel model) {
            super(model);
        }
        
        @Override
        protected Document extractDomDocument(String content)
                throws Exception {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(extractString(content)));
            return builder.parse(is);
        }
                                                                                                                                                                                                                                                                                                                                                                                                                                
        @Override
        protected String extractString(String content) throws Exception {
            return content;
        }
    }
    
}
