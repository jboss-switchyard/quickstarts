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
package org.switchyard.security.pull;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import org.switchyard.common.io.pull.Puller;

/**
 * Utility class to safely access ("pull") KeyStores from various sources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class KeyStorePuller extends Puller<KeyStore> {

    private final String _type;
    private final char[] _password;

    /**
     * Constructs a new KeyStorePuller with the default KeyStore type and null KeyStore password.
     */
    public KeyStorePuller() {
        this(null, null);
    }

    /**
     * Constructs a new KeyStorePuller with the specified KeyStore type and null KeyStore password.
     * @param type the KeyStore type
     */
    public KeyStorePuller(String type) {
        this(type, null);
    }

    /**
     * Constructs a new KeyStorePuller with the default KeyStore type and specified KeyStore password.
     * @param password the KeyStore password
     */
    public KeyStorePuller(char[] password) {
        this(null, password);
    }

    /**
     * Constructs a new KeyStorePuller with the specified KeyStore type and specified KeyStore password.
     * @param type the KeyStore type
     * @param password the KeyStore password
     */
    public KeyStorePuller(String type, char[] password) {
        _type = type != null ? type : KeyStore.getDefaultType();
        _password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KeyStore pull(InputStream stream) throws IOException {
        try {
            KeyStore keyStore = KeyStore.getInstance(_type);
            keyStore.load(stream, _password);
            return keyStore;
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse);
        }
    }

}
