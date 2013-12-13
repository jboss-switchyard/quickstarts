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

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.switchyard.component.common.selector.BaseOperationSelector;
import org.switchyard.component.jca.JCAMessages;
import org.switchyard.component.jca.composer.JMSBindingData;
import org.switchyard.config.model.selector.OperationSelectorModel;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * OperationSelector for JMS Message.
 */
public class JMSOperationSelector extends BaseOperationSelector<JMSBindingData> {

    /** key name for lookup the operation from map. */
    public static final String KEY = "operationSelector";

    /**
     * Constructor.
     * @param model OperationSelectorModel.
     */
    public JMSOperationSelector(OperationSelectorModel model) {
        super(model);
    }

    @Override
    protected Document extractDomDocument(JMSBindingData binding) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(extractString(binding)));
        return builder.parse(is);
    }

    @Override
    protected String extractString(JMSBindingData binding) throws Exception {
        Message content = binding.getMessage();
        if (content instanceof TextMessage) {
            return TextMessage.class.cast(content).getText();
        
        } else if (content instanceof BytesMessage) {
            BytesMessage sourceBytes = BytesMessage.class.cast(content);
            if (sourceBytes.getBodyLength() > Integer.MAX_VALUE) {
                throw JCAMessages.MESSAGES.theSizeOfMessageContentExceedsBytesThatIsNotSupportedByThisOperationSelector("" + Integer.MAX_VALUE);
            }
            byte[] bytearr = new byte[(int)sourceBytes.getBodyLength()];
            sourceBytes.readBytes(bytearr);
            return new String(bytearr);

        } else if (content instanceof ObjectMessage) {
            ObjectMessage sourceObj = ObjectMessage.class.cast(content);
            return String.class.cast(sourceObj.getObject());

        } else if (content instanceof MapMessage) {
            MapMessage sourceMap = MapMessage.class.cast(content);
            return sourceMap.getString(KEY);
        } else {
            return content.getStringProperty(KEY);
        }
    }

}
