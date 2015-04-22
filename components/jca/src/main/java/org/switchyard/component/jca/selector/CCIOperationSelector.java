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
package org.switchyard.component.jca.selector;

import java.io.StringReader;

import javax.resource.cci.MappedRecord;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.switchyard.component.common.selector.BaseOperationSelector;
import org.switchyard.component.jca.composer.MappedRecordBindingData;
import org.switchyard.config.model.selector.OperationSelectorModel;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * An example of OperationSelector implementation for CCI MappedRecord.
 */
public class CCIOperationSelector extends BaseOperationSelector<MappedRecordBindingData> {

    /** key name for lookup the operation from map. */
    public static final String KEY = "operationSelector";
    
    /**
     * Constructor.
     * @param model OperationSelectorModel
     */
    public CCIOperationSelector(OperationSelectorModel model) {
        super(model);
    }

    @Override
    protected Document extractDomDocument(MappedRecordBindingData binding) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(extractString(binding)));
        return builder.parse(is);
    }

    @Override
    protected String extractString(MappedRecordBindingData binding) throws Exception {
        MappedRecord content = binding.getRecord();
        return String.class.cast(content.get(KEY));
    }

}
