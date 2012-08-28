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
package org.switchyard.component.jca.selector;

import java.io.StringReader;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.switchyard.component.common.selector.OperationSelector;
import org.switchyard.component.common.selector.config.model.OperationSelectorModel;
import org.switchyard.component.jca.composer.JMSBindingData;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * OperationSelector for JMS Message.
 */
public class JMSOperationSelector extends OperationSelector<JMSBindingData> {

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
                throw new Exception("The size of message content exceeds "
                        + Integer.MAX_VALUE + " bytes, that is not supported by this OperationSelector");
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
