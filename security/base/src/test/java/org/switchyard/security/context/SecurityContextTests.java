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
package org.switchyard.security.context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.crypto.SealedObject;
import javax.security.auth.Subject;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.common.io.pull.ElementPuller;
import org.switchyard.security.credential.AssertionCredential;
import org.switchyard.security.credential.ConfidentialityCredential;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.NameCredential;
import org.switchyard.security.credential.PasswordCredential;
import org.switchyard.security.credential.PrincipalCredential;
import org.switchyard.security.credential.extractor.SOAPMessageCredentialExtractorTests;
import org.switchyard.security.crypto.PrivateCrypto;
import org.switchyard.security.principal.GroupPrincipal;
import org.switchyard.security.principal.RolePrincipal;
import org.switchyard.security.principal.UserPrincipal;

/**
 * SecurityContext tests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SecurityContextTests {

    @Test
    public void testCredentialsSerializable() throws Exception {
        for (Credential expected : getCredentials()) {
            Credential actual = (Credential)serDeser(expected);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void testCredentialsSealable() throws Exception {
        for (Credential expected : getCredentials()) {
            Credential actual = (Credential)sealUnseal(expected);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void testContextSerializable() throws Exception {
        SecurityContext expected = getContext();
        SecurityContext actual = (SecurityContext)serDeser(expected);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testContextSealable() throws Exception {
        SecurityContext expected = getContext();
        SecurityContext actual = (SecurityContext)sealUnseal(expected);
        Assert.assertEquals(expected, actual);
    }

    private SecurityContext getContext() throws Exception {
        SecurityContext context = new DefaultSecurityContext();
        Subject subject = context.getSubject("testSecurityDomain");
        subject.getPrincipals().add(new UserPrincipal("testUser"));
        context.getCredentials().addAll(getCredentials());
        return context;
    }

    private Set<Credential> getCredentials() throws Exception {
        UserPrincipal user = new UserPrincipal("testUser");
        RolePrincipal role = new RolePrincipal("testRole");
        GroupPrincipal parentGroup = new GroupPrincipal("testParentGroup");
        parentGroup.addMember(user);
        GroupPrincipal childGroup = new GroupPrincipal("testChildGroup");
        childGroup.addMember(role);
        parentGroup.addMember(childGroup);
        Set<Credential> creds = new LinkedHashSet<Credential>();
        creds.add(new AssertionCredential(new ElementPuller().pull(new StringReader("<testAssertion/>"))));
        creds.add(SOAPMessageCredentialExtractorTests.getBinarySecurityTokenCertificateCredential());
        creds.add(new ConfidentialityCredential(true));
        creds.add(new ConfidentialityCredential(false));
        creds.add(new NameCredential("testName"));
        creds.add(new PasswordCredential("testPassword"));
        creds.add(new PrincipalCredential(user, true));
        creds.add(new PrincipalCredential(role, false));
        creds.add(new PrincipalCredential(childGroup, true));
        creds.add(new PrincipalCredential(parentGroup, false));
        return creds;
    }

    private Object serDeser(Serializable o) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.flush();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        o = (Serializable)ois.readObject();
        ois.close();
        return o;
    }

    private Object sealUnseal(Serializable o) throws Exception {
        PrivateCrypto pc = new PrivateCrypto("TripleDES", 168);
        SealedObject so = pc.seal(o);
        o = pc.unseal(so);
        return o;
    }

}
