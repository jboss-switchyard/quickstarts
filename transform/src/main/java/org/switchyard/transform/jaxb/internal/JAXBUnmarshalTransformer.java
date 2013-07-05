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

package org.switchyard.transform.jaxb.internal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.switchyard.Message;
import org.switchyard.SwitchYardException;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.config.model.Scannable;
import org.switchyard.transform.BaseTransformer;

/**
 * JAXB Unmarshalling transformer.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 *
 * @param <F> From Type
 * @param <T> To Type.
 */
@Scannable(false)
public class JAXBUnmarshalTransformer<F, T> extends BaseTransformer<Message, Message> {

    private JAXBContext _jaxbContext;

    /**
     * Public constructor.
     * @param from From type.
     * @param to To type.
     * @param contextPath JAXB context path (Java package).
     * @throws SwitchYardException Failed to create JAXBContext.
     */
    public JAXBUnmarshalTransformer(QName from, QName to, String contextPath) throws SwitchYardException {
        super(from, to);
        try {
            if (contextPath != null) {
                _jaxbContext = JAXBContext.newInstance(contextPath);
            } else {
                _jaxbContext = JAXBContext.newInstance(QNameUtil.toJavaMessageType(to));
            }
        } catch (JAXBException e) {
            throw new SwitchYardException("Failed to create JAXBContext for '" + to + "'.", e);
        }
    }

    @Override
    public Message transform(Message message) {
        Unmarshaller unmarshaller;

        try {
            unmarshaller = _jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new SwitchYardException("Failed to create Unmarshaller for '" + getTo() + "'.", e);
        }

        try {
            Object unmarshalledObject = unmarshaller.unmarshal(message.getContent(Source.class));

            if (unmarshalledObject instanceof JAXBElement) {
                message.setContent(((JAXBElement)unmarshalledObject).getValue());
            } else {
                message.setContent(unmarshalledObject);
            }
        } catch (JAXBException e) {
            throw new SwitchYardException("Failed to unmarshall for '" + getTo() + "'.", e);
        }

        return message;
    }
}
