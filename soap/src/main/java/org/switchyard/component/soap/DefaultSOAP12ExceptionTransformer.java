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

package org.switchyard.component.soap;

import java.lang.reflect.InvocationTargetException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.config.model.Scannable;
import org.switchyard.transform.BaseTransformer;

/**
 * Default {@link Exception} to SOAP 1.2 fault transformer.
 *
 * @param <F> From type.
 * @param <T> To type.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
@Scannable(false)
public class DefaultSOAP12ExceptionTransformer<F extends Exception, T extends SOAPMessage> extends BaseTransformer<F, T> {

    @Override
    public QName getFrom() {
        return toMessageType(Exception.class);
    }

    @Override
    public QName getTo() {
        return SOAPUtil.SOAP12_FAULT_MESSAGE_TYPE;
    }

    @Override
    public SOAPMessage transform(Exception from) {
        try {
            Throwable cause = from.getCause();
            if (cause instanceof InvocationTargetException) {
                return SOAPUtil.generateSOAP12Fault(cause.getCause());
            } else {
                return SOAPUtil.generateSOAP12Fault(from);
            }
        } catch (SOAPException e1) {
            // TODO: We're in a fault on a fault type situation now... should generateFault be throwing exceptions??
            throw new IllegalStateException("Unexpected SOAPException when generating a SOAP 1.2 Fault message.", from);
        }
    }
}
