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
