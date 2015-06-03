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
package org.switchyard.component.soap.composer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.common.io.pull.ElementPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.composer.BaseRegexContextMapper;
import org.switchyard.component.common.label.ComponentLabel;
import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.config.Configuration;
import org.switchyard.config.ConfigurationPuller;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * SOAPContextMapper.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class SOAPContextMapper extends BaseRegexContextMapper<SOAPBindingData> {

    /**
     * The HTTP responce code.
     */
    public static final String HTTP_RESPONSE_STATUS = "http_response_status";
    /**
    * Headers to be excluded.
    */
    public static final List<String> HTTP_HEADERS_EXCLUDED = Arrays.asList(new String[]{"content-type", "content-length"});
    private static final String[] SOAP_HEADER_LABELS = new String[]{ComponentLabel.SOAP.label(), EndpointLabel.SOAP.label()};
    private static final String[] SOAP_MIME_LABELS = new String[]{ComponentLabel.SOAP.label(), EndpointLabel.HTTP.label()};

    private SOAPHeadersType _soapHeadersType = null;

    /**
     * Gets the SOAPHeadersType.
     * @return the SOAPHeadersType
     */
    public SOAPHeadersType getSOAPHeadersType() {
        return _soapHeadersType;
    }

    /**
     * Sets the SOAPHeadersType.
     * @param soapHeadersType the SOAPHeadersType
     * @return this instance (useful for chaining)
     */
    public SOAPContextMapper setSOAPHeadersType(SOAPHeadersType soapHeadersType) {
        _soapHeadersType = soapHeadersType;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(SOAPBindingData source, Context context) throws Exception {
        SOAPMessage soapMessage = source.getSOAPMessage();
        if (soapMessage.getSOAPBody().hasFault() && (source.getSOAPFaultInfo() != null)) {
            context.setProperty(SOAPComposition.SOAP_FAULT_INFO, source.getSOAPFaultInfo(), Scope.EXCHANGE).addLabels(SOAP_HEADER_LABELS);
        }
        if (source.getStatus() != null) {
            context.setProperty(HTTP_RESPONSE_STATUS, source.getStatus()).addLabels(SOAP_MIME_LABELS);
        }
        for (String key : source.getHttpHeaders().keySet()) {
            if (matches(key)) {
                List<String> values = source.getHttpHeaders().get(key);
                if (values != null) {
                    context.setProperty(key, values).addLabels(SOAP_MIME_LABELS);
                }
            }
        }

        @SuppressWarnings("unchecked")
        Iterator<SOAPHeaderElement> soapHeaders = soapMessage.getSOAPHeader().examineAllHeaderElements();
        while (soapHeaders.hasNext()) {
            SOAPHeaderElement soapHeader = soapHeaders.next();
            QName qname = soapHeader.getElementQName();
            if (matches(qname)) {
                final Object value;
                switch (_soapHeadersType != null ? _soapHeadersType : SOAPHeadersType.VALUE) {
                    case CONFIG:
                        value = new ConfigurationPuller().pull(soapHeader);
                        break;
                    case DOM:
                        value = soapHeader;
                        break;
                    case VALUE:
                        value = soapHeader.getValue();
                        break;
                    case XML:
                        value = new ConfigurationPuller().pull(soapHeader).toString();
                        break;
                    default:
                        value = null;
                }
                if (value != null) {
                    String name = qname.toString();
                    context.setProperty(name, value).addLabels(SOAP_HEADER_LABELS);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapTo(Context context, SOAPBindingData target) throws Exception {
        SOAPMessage soapMessage = target.getSOAPMessage();
        SOAPHeader soapHeader = soapMessage.getSOAPHeader();
        for (Property property : context.getProperties()) {
            Object value = property.getValue();
            if (value != null) {
                String name = property.getName();
                QName qname = XMLHelper.createQName(name);
                boolean qualifiedForSoapHeader = Strings.trimToNull(qname.getNamespaceURI()) != null;
                if (qualifiedForSoapHeader && matches(qname)) {
                    if (value instanceof Node) {
                        Node domNode = soapHeader.getOwnerDocument().importNode((Node)value, true);
                        soapHeader.appendChild(domNode);
                    } else if (value instanceof Configuration) {
                        Element configElement = new ElementPuller().pull(new StringReader(value.toString()));
                        Node configNode = soapHeader.getOwnerDocument().importNode(configElement, true);
                        soapHeader.appendChild(configNode);
                    } else {
                        String v = value.toString();
                        if (SOAPHeadersType.XML.equals(_soapHeadersType)) {
                            try {
                                Element xmlElement = new ElementPuller().pull(new StringReader(v));
                                Node xmlNode = soapHeader.getOwnerDocument().importNode(xmlElement, true);
                                soapHeader.appendChild(xmlNode);
                            } catch (Throwable t) {
                                soapHeader.addChildElement(qname).setValue(v);
                            }
                        } else {
                            soapHeader.addChildElement(qname).setValue(v);
                        }
                    }
                } else if (matches(name) || property.hasLabel(EndpointLabel.HTTP.label())) {
                    if (HTTP_RESPONSE_STATUS.equalsIgnoreCase(name)) {
                        if (value instanceof String) {
                            target.setStatus(Integer.parseInt((String) value));
                        } else if (value instanceof Integer) {
                            target.setStatus((Integer) value);
                        }
                    } else if (HTTP_HEADERS_EXCLUDED.contains(name.toLowerCase())) {
                        // Excluding HTTP headers which should not be set manually
                        continue;
                    } else {
                        if (value instanceof List) {
                            List<String> stringValues = new ArrayList<String>();
                            for (Object v : List.class.cast(value)) {
                                if (v == null || v instanceof String) {
                                    stringValues.add((String)v);
                                }
                            }
                            if (!stringValues.isEmpty()) {
                                target.getHttpHeaders().put(name, stringValues);
                            }
                        } else if (value instanceof String) {
                            target.getHttpHeaders().put(name, Collections.singletonList((String)value));
                        }
                    }
                }
            }
        }
    }

}
