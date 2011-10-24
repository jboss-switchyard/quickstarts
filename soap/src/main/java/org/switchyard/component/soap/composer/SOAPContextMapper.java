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
        SOAPHeader soapHeader = source.getSOAPHeader();
        @SuppressWarnings("unchecked")
        Iterator<SOAPHeaderElement> iter = soapHeader.examineAllHeaderElements();
        while (iter.hasNext()) {
            SOAPHeaderElement elem = iter.next();
            QName qname = elem.getElementQName();
            if (matches(qname)) {
                String value = elem.getValue();
                if (value != null) {
                    String name = qname.toString();
                    context.setProperty(name, value);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapTo(Context context, SOAPMessage target) throws Exception {
        SOAPHeader soapHeader = target.getSOAPHeader();
        for (Scope scope : Scope.values()) {
            for (Property property : context.getProperties(scope)) {
                QName name = XMLHelper.createQName(property.getName());
                boolean qualifiedForSoapHeader = Strings.trimToNull(name.getNamespaceURI()) != null;
                if (qualifiedForSoapHeader && matches(name)) {
                    Object value = property.getValue();
                    if (value != null) {
                        soapHeader.addChildElement(name).setValue(String.valueOf(value));
                    }
                }
            }
        }
    }

}
