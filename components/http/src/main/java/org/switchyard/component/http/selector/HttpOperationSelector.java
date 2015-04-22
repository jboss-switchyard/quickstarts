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
package org.switchyard.component.http.selector;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.switchyard.component.common.selector.BaseOperationSelector;
import org.switchyard.component.http.composer.HttpBindingData;
import org.switchyard.config.model.selector.OperationSelectorModel;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * OperationSelector implementation for HTTP binding.
 */
public class HttpOperationSelector extends BaseOperationSelector<HttpBindingData> {

    /**
     * Constructor.
     * @param model OperationSelectorModel
     */
    public HttpOperationSelector(OperationSelectorModel model) {
        super(model);
    }

    @Override
    protected Document extractDomDocument(HttpBindingData content)
            throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(extractString(content)));
        return builder.parse(is);
    }

    @Override
    protected String extractString(HttpBindingData content) throws Exception {
        return content.getBodyAsString();
    }

}
