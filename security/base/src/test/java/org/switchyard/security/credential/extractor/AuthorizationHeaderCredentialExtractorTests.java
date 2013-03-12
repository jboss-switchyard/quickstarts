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

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.NameCredential;
import org.switchyard.security.credential.PasswordCredential;

/**
 * AuthorizationHeaderCredentialExtractor tests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class AuthorizationHeaderCredentialExtractorTests {

    private static final String BASE_PATH = "/org/switchyard/security/credential/extractor/AuthorizationHeaderCredentialExtractorTests-";
    private static final String BASIC_TXT = BASE_PATH + "Basic.txt";
    private static final String DIGEST_TXT = BASE_PATH + "Digest.txt";

    @Test
    public void testBasic() throws Exception {
        String source = new StringPuller().pull(BASIC_TXT, AuthorizationHeaderCredentialExtractorTests.class);
        Set<Credential> creds = new AuthorizationHeaderCredentialExtractor().extract(source);
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
    public void testDigest() throws Exception {
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // https://issues.jboss.org/browse/SWITCHYARD-1082
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        String source = new StringPuller().pull(DIGEST_TXT, AuthorizationHeaderCredentialExtractorTests.class);
        Set<Credential> creds = new AuthorizationHeaderCredentialExtractor().extract(source);
        boolean foundName = false;
        for (Credential cred : creds) {
            if (cred instanceof NameCredential) {
                foundName = true;
                String name = ((NameCredential)cred).getName();
                Assert.assertEquals("Mufasa", name);
            }
        }
        if (!foundName) {
            Assert.fail("name not found");
        }
        // TODO: complete per SWITCHYARD-1082
    }

}
