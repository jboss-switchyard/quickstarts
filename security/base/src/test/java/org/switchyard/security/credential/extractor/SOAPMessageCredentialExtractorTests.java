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
package org.switchyard.security.credential.extractor;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.Set;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.security.credential.AssertionCredential;
import org.switchyard.security.credential.CertificateCredential;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.NameCredential;
import org.switchyard.security.credential.PasswordCredential;
import org.w3c.dom.Element;

/**
 * SOAPMessageCredentialExtractor tests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class SOAPMessageCredentialExtractorTests {

    private static final String BASE_PATH = "/org/switchyard/security/credential/extractor/SOAPMessageCredentialExtractorTests-";
    private static final String ASSERTION_XML = BASE_PATH + "Assertion.xml";
    private static final String USERNAME_TOKEN_XML = BASE_PATH + "UsernameToken.xml";
    private static final String BINARY_SECURITY_TOKEN_XML = BASE_PATH + "BinarySecurityToken.xml";

    @Test
    public void testAssertion() throws Exception {
        SOAPMessage source = createMessage(ASSERTION_XML);
        Set<Credential> creds = new SOAPMessageCredentialExtractor().extract(source);
        boolean foundAssertion = false;
        for (Credential cred : creds) {
            if (cred instanceof AssertionCredential) {
                foundAssertion = true;
                Element assertion = ((AssertionCredential)cred).getAssertion();
                Assert.assertEquals("ID_00cdd057-e611-439d-a189-581e1437e560", assertion.getAttribute("ID"));
            }
        }
        if (!foundAssertion) {
            Assert.fail("assertion not found");
        }
    }

    @Test
    public void testUsernameToken() throws Exception {
        SOAPMessage source = createMessage(USERNAME_TOKEN_XML);
        Set<Credential> creds = new SOAPMessageCredentialExtractor().extract(source);
        boolean foundName = false;
        boolean foundPassword = false;
        for (Credential cred : creds) {
            if (cred instanceof NameCredential) {
                foundName = true;
                String name = ((NameCredential)cred).getName();
                Assert.assertEquals("Aladdin", name);
            } else if (cred instanceof PasswordCredential) {
                foundPassword = true;
                String password = new String(((PasswordCredential)cred).getPassword());
                Assert.assertEquals("open sesame", password);
            }
        }
        if (!foundName) {
            Assert.fail("name not found");
        }
        if (!foundPassword) {
            Assert.fail("password not found");
        }
    }

    @Test
    public void testBinarySecurityToken() throws Exception {
        SOAPMessage source = createMessage(BINARY_SECURITY_TOKEN_XML);
        Set<Credential> creds = new SOAPMessageCredentialExtractor().extract(source);
        boolean foundCertificate = false;
        for (Credential cred : creds) {
            if (cred instanceof CertificateCredential) {
                foundCertificate = true;
                Certificate certificate = ((CertificateCredential)cred).getCertificate();
                Assert.assertEquals("X.509", certificate.getType());
            }
        }
        if (!foundCertificate) {
            Assert.fail("certificate not found");
        }
    }

    private SOAPMessage createMessage(String path) throws Exception {
        InputStream is = Classes.getResourceAsStream(path, getClass());
        try {
            return MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage(null, is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

}
