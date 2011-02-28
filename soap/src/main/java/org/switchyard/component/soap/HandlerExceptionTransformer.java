/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.component.soap;

import java.lang.reflect.InvocationTargetException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.switchyard.HandlerException;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.component.soap.util.WSDLUtil;
import org.switchyard.transform.BaseTransformer;

/**
 * Base {@link HandlerException} to SOAP fault transformer.
 *
 * @param <F> From type.
 * @param <T> To type.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class HandlerExceptionTransformer<F extends HandlerException, T extends SOAPMessage> extends BaseTransformer<F, T> {

    @Override
    public QName getFrom() {
        return new QName(HandlerException.MESSAGE_TYPE);
    }

    @Override
    public QName getTo() {
        return WSDLUtil.SOAP_FAULT_MESSAGE_TYPE;
    }

    @Override
    public SOAPMessage transform(HandlerException from) {
        try {
            Throwable cause = from.getCause();
            if (cause instanceof InvocationTargetException) {
                return SOAPUtil.generateFault(cause.getCause());
            } else {
                return SOAPUtil.generateFault(from);
            }
        } catch (SOAPException e1) {
            // TODO: We're in a fault on a fault type situation now... should generateFault be throwing exceptions??
            throw new IllegalStateException("Unexpected SOAPException when generating a SOAP Fault message.", from);
        }
    }
}
