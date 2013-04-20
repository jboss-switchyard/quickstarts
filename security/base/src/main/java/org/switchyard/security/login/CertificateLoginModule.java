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
package org.switchyard.security.login;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.io.pull.Puller.PathType;
import org.switchyard.common.lang.Strings;
import org.switchyard.security.BaseSecurityMessages;
import org.switchyard.security.callback.CertificateCallback;
import org.switchyard.security.principal.Group;
import org.switchyard.security.principal.Role;
import org.switchyard.security.principal.User;
import org.switchyard.security.pull.KeyStorePuller;

/**
 * CertificateLoginModule.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class CertificateLoginModule extends SwitchYardLoginModule {

    private X509Certificate _verifiedCallerCertificate;

    /**
     * Constructs a new CertificateLoginModule.
     */
    public CertificateLoginModule() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean login() throws LoginException {
        NameCallback aliasCallback = new NameCallback("alias");
        //PasswordCallback keyPasswordCallback = new PasswordCallback("keyPassword", false);
        CertificateCallback certificateCallback = new CertificateCallback();
        try {
            //getCallbackHandler().handle(new Callback[]{aliasCallback, keyPasswordCallback, certificateCallback});
            getCallbackHandler().handle(new Callback[]{aliasCallback, certificateCallback});
        } catch (IOException ioe) {
            throw BaseSecurityMessages.MESSAGES.failedInvokeCallback(ioe.getMessage(), ioe);
        } catch (UnsupportedCallbackException uce) {
            throw BaseSecurityMessages.MESSAGES.callbackHandlerNoSupport(uce.getCallback().toString());
        }
        X509Certificate callerCertificate = getCallerCertificate(certificateCallback);
        KeyStore keyStore = getKeyStore();
        String alias = aliasCallback.getName();
        //char[] keyPassword = keyPasswordCallback.getPassword();
        Certificate switchyardCertificate;
        try {
            switchyardCertificate = keyStore.getCertificate(alias);
        } catch (KeyStoreException kse) {
            throw BaseSecurityMessages.MESSAGES.problemAccessingKeystore(kse.getMessage());
        }
        try {
            callerCertificate.verify(switchyardCertificate.getPublicKey());
        } catch (Exception e) {
            throw BaseSecurityMessages.MESSAGES.problemVerifyingCallerCert(e.getMessage());
        }
        _verifiedCallerCertificate = callerCertificate;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean commit() throws LoginException {
        if (_verifiedCallerCertificate == null) {
            return false;
        } else {
            Set<Principal> principals = getSubject().getPrincipals();
            String userName = _verifiedCallerCertificate.getSubjectX500Principal().getName();
            // get the CN from the DN.
            userName = userName.substring(userName.indexOf('=') + 1, userName.indexOf(','));
            User authenticatedPrincipal = new User(userName);
            principals.add(authenticatedPrincipal);
            // maybe add roles
            Properties rolesProperties = getRolesProperties();
            if (rolesProperties != null) {
                Set<Group> groups = getSubject().getPrincipals(Group.class);
                Set<String> roleNames = Strings.uniqueSplitTrimToNull(rolesProperties.getProperty(userName), ",");
                for (String roleName : roleNames) {
                    Role role = new Role(roleName);
                    if (groups.isEmpty()) {
                        Group rolesGroup = new Group(Group.ROLES);
                        rolesGroup.addMember(role);
                        getSubject().getPrincipals().add(rolesGroup);
                    } else {
                        for (Group group : groups) {
                            if (Group.ROLES.equals(group.getName())) {
                                group.addMember(role);
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean logout() throws LoginException {
        _verifiedCallerCertificate = null;
        return true;
    }

    private X509Certificate getCallerCertificate(CertificateCallback certificateCallback) throws LoginException {
        X509Certificate x509cert = null;
        for (Certificate cert : certificateCallback.getCertificates()) {
            if (cert instanceof X509Certificate) {
                x509cert = (X509Certificate)cert;
                break;
            }
        }
        if (x509cert == null) {
            throw BaseSecurityMessages.MESSAGES.noCallerCertificateProvided();
        }
        return x509cert;
    }

    private KeyStore getKeyStore() throws LoginException {
        String keyStoreLocation = getOption("keyStoreLocation", true);
        String keyStoreType = getOption("keyStoreType", false);
        String keyStorePassword = getOption("keyStorePassword", false);
        KeyStorePuller keyStorePuller = new KeyStorePuller(keyStoreType, keyStorePassword != null ? keyStorePassword.toCharArray() : null);
        return keyStorePuller.pullPath(keyStoreLocation, getClass(), PathType.values());
    }

    private Properties getRolesProperties() {
        String rolesPropertiesFile = getOption("rolesProperties", false);
        if (rolesPropertiesFile != null) {
            return new PropertiesPuller().pullPath(rolesPropertiesFile, getClass(), PathType.values());
        }
        return null;
    }

}
