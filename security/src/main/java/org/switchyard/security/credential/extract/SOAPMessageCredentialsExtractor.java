/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.security.credential.extract;

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
 * SOAPMessageCredentialsExtractor.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class SOAPMessageCredentialsExtractor implements CredentialsExtractor<SOAPMessage> {

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
    public SOAPMessageCredentialsExtractor() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extractCredentials(SOAPMessage source) {
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
                                byte[] keyBytes = null;
                                if ("Base64Binary".equalsIgnoreCase(encodingType)) {
                                    keyBytes = Base64.decode(certString).getBytes();
                                } else {
                                    keyBytes = certString.getBytes();
                                }
                                try {
                                    CertificateFactory factory = CertificateFactory.getInstance(certificateMatch(valueType));
                                    InputStream in = new ByteArrayInputStream(keyBytes);
                                    if (X509PKIPATHV1.equals(valueType)) {
                                        CertPath path = factory.generateCertPath(in);
                                        for (Certificate certificate : path.getCertificates()) {
                                            credentials.add(new CertificateCredential(certificate));
                                        }
                                    } else if (X509V3.equals(valueType)) {
                                        Certificate certificate = factory.generateCertificate(in);
                                        credentials.add(new CertificateCredential(certificate));
                                    } else if (PKCS7.equals(valueType)) {
                                        throw new IllegalArgumentException(valueType + " not implemented (although recognized)");
                                    } else {
                                        throw new IllegalArgumentException(valueType + " not implemented");
                                    }
                                } catch (CertificateException ce) {
                                    throw new RuntimeException("Could not create certificate(s): " + ce.getMessage(), ce);
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
