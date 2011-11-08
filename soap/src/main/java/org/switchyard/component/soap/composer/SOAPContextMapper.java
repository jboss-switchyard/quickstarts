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
package org.switchyard.component.soap.composer;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.composer.BaseContextMapper;

/**
 * SOAPContextMapper.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SOAPContextMapper extends BaseContextMapper<SOAPMessage> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(SOAPMessage source, Context context) throws Exception {
        @SuppressWarnings("unchecked")
        Iterator<MimeHeader> mimeHeaders = source.getMimeHeaders().getAllHeaders();
        while (mimeHeaders.hasNext()) {
            MimeHeader mimeHeader = mimeHeaders.next();
            String name = mimeHeader.getName();
            if (matches(name)) {
                String value = mimeHeader.getValue();
                if (value != null) {
                    // SOAPMessage MIME headers -> Context IN properties
                    context.setProperty(name, value, Scope.IN);
                }
            }
        }
        @SuppressWarnings("unchecked")
        Iterator<SOAPHeaderElement> soapHeaders = source.getSOAPHeader().examineAllHeaderElements();
        while (soapHeaders.hasNext()) {
            SOAPHeaderElement soapHeader = soapHeaders.next();
            QName qname = soapHeader.getElementQName();
            if (matches(qname)) {
                String value = soapHeader.getValue();
                if (value != null) {
                    // SOAPMessage SOAP headers -> Context EXCHANGE properties
                    context.setProperty(qname.toString(), value, Scope.EXCHANGE);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapTo(Context context, SOAPMessage target) throws Exception {
        MimeHeaders mimeHeaders = target.getMimeHeaders();
        for (Property property : context.getProperties(Scope.OUT)) {
            String name = property.getName();
            if (matches(name)) {
                Object value = property.getValue();
                if (value != null) {
                    // Context OUT properties -> SOAPMessage MIME headers
                    mimeHeaders.addHeader(name, String.valueOf(value));
                }
            }
        }
        SOAPHeader soapHeader = target.getSOAPHeader();
        for (Property property : context.getProperties(Scope.EXCHANGE)) {
            String name = property.getName();
            QName qname = XMLHelper.createQName(name);
            boolean qualifiedForSoapHeader = Strings.trimToNull(qname.getNamespaceURI()) != null;
            if (qualifiedForSoapHeader && matches(qname)) {
                Object value = property.getValue();
                if (value != null) {
                    // Context EXCHANGE properties -> SOAPMessage SOAP headers
                    soapHeader.addChildElement(qname).setValue(String.valueOf(value));
                }
            }
        }
    }

}
