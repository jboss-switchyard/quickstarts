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
package org.switchyard.security.credential.extractor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.switchyard.common.codec.Base64;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.security.BaseSecurityMessages;
import org.switchyard.security.credential.AssertionCredential;
import org.switchyard.security.credential.CertificateCredential;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.NameCredential;
import org.switchyard.security.credential.PasswordCredential;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * SOAPMessageCredentialExtractor.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class SOAPMessageCredentialExtractor implements CredentialExtractor<SOAPMessage> {

    private static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String WSSE_NS2 = "http://schemas.xmlsoap.org/ws/2002/04/secext";
    private static final String WSSE11_NS = "http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd";
    private static final String WSSE_LN = "Security";
    private static final QName WSSE_QNAME = new QName(WSSE_NS, WSSE_LN);
    private static final QName WSSE_2_QNAME = new QName(WSSE_NS2, WSSE_LN);
    private static final QName WSSE_11_QNAME = new QName(WSSE11_NS, WSSE_LN);

    private static final String X509V3 = "X509v3";
    private static final String X509PKIPATHV1 = "X509PKIPathv1";
    private static final String PKCS7 = "PKCS7";

    /**
     * Constructs a new SOAPMessageCredentialsExtractor.
     */
    public SOAPMessageCredentialExtractor() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extract(SOAPMessage source) {
        Set<Credential> credentials = new HashSet<Credential>();
        if (source != null) {
            try {
                SOAPHeaderElement securityHeader = getSecurityHeader(source.getSOAPPart().getEnvelope());
                if (securityHeader != null) {
                    Iterator<?> iter = securityHeader.getChildElements();
                    while (iter.hasNext()) {
                        Node securityTokenNode = (Node)iter.next();
                        if (securityTokenNode.getNodeType() == Node.ELEMENT_NODE) {
                            String securityTokenNodeName = XMLHelper.nameOf(securityTokenNode);
                            if ("Assertion".equalsIgnoreCase(securityTokenNodeName)) {
                                credentials.add(new AssertionCredential((Element)securityTokenNode));
                            } else if ("UsernameToken".equalsIgnoreCase(securityTokenNodeName)) {
                                NodeList usernameChildElements = securityTokenNode.getChildNodes();
                                for (int i=0; i < usernameChildElements.getLength(); i++) {
                                    Node usernameChildNode = usernameChildElements.item(i);
                                    String usernameChildeNodeName = XMLHelper.nameOf(usernameChildNode);
                                    if ("Username".equalsIgnoreCase(usernameChildeNodeName)) {
                                        String name = XMLHelper.valueOf(usernameChildNode.getFirstChild());
                                        if (name != null) {
                                            credentials.add(new NameCredential(name));
                                        }
                                    } else if ("Password".equalsIgnoreCase(usernameChildeNodeName)) {
                                        String password =  XMLHelper.valueOf(usernameChildNode.getFirstChild());
                                        if (password != null) {
                                            credentials.add(new PasswordCredential(password));
                                        }
                                    }
                                }
                            } else if ("BinarySecurityToken".equalsIgnoreCase(securityTokenNodeName)) {
                                NamedNodeMap attributes = securityTokenNode.getAttributes();
                                String encodingType = stripNS(XMLHelper.valueOf(attributes.getNamedItem("EncodingType")));
                                String valueType = stripNS(XMLHelper.valueOf(attributes.getNamedItem("ValueType")));
                                String certString = XMLHelper.valueOf(securityTokenNode.getFirstChild());
                                byte[] certBytes = null;
                                if ("Base64Binary".equalsIgnoreCase(encodingType)) {
                                    certBytes = Base64.decode(certString);
                                } else {
                                    certBytes = certString.getBytes();
                                }
                                try {
                                    CertificateFactory factory = CertificateFactory.getInstance(certificateMatch(valueType));
                                    InputStream certStream = new ByteArrayInputStream(certBytes);
                                    if (X509PKIPATHV1.equals(valueType)) {
                                        CertPath path = factory.generateCertPath(certStream);
                                        for (Certificate certificate : path.getCertificates()) {
                                            credentials.add(new CertificateCredential(certificate));
                                        }
                                    } else if (X509V3.equals(valueType)) {
                                        Certificate certificate = factory.generateCertificate(certStream);
                                        credentials.add(new CertificateCredential(certificate));
                                    } else if (PKCS7.equals(valueType)) {
                                        throw BaseSecurityMessages.MESSAGES.valueTypeRecognizedNotImplemented(valueType);
                                    } else {
                                        throw BaseSecurityMessages.MESSAGES.valueTypeNotImplemented(valueType);
                                    }
                                } catch (CertificateException ce) {
                                    throw BaseSecurityMessages.MESSAGES.couldNotCreateCert(ce.getMessage(), ce);
                                }
                            }
                        }
                    }
                }
            } catch (SOAPException se) {
                throw new RuntimeException(se);
            }
        }
        return credentials;
    }

    private SOAPHeaderElement getSecurityHeader(SOAPEnvelope envelope) throws SOAPException {
        if (envelope != null) {
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                Iterator<?> iter = header.getChildElements(WSSE_QNAME);
                if (iter.hasNext()) {
                    return (SOAPHeaderElement)iter.next();
                }
                iter = header.getChildElements(WSSE_2_QNAME);
                if (iter.hasNext()) {
                    return (SOAPHeaderElement)iter.next();
                }
                iter = header.getChildElements(WSSE_11_QNAME);
                if (iter.hasNext()) {
                    return (SOAPHeaderElement)iter.next();
                }
            }
        }
        return null;
    }

    private String stripNS(String value) {
        if (value != null) {
            if (value.startsWith("http")) {
                int idx = value.indexOf('#');
                if (idx > 0) {
                    value = value.substring(idx + 1);
                }
            } else {
                int idx = value.indexOf(':');
                if (idx > 0) {
                    value = value.substring(idx + 1);
                }
            }
        }
        return value;
    }

    private String certificateMatch(String valueType) {
        if (valueType.startsWith("X509")) {
            return "X.509";
        }
        return valueType;
    }

    /*
    private String pathMatch(String valueType) {
        if (valueType.startsWith("")) {
            return "PkiPath";
        }
        return "PKCS7";
    }
    */

}
