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
package org.switchyard.security.crypto;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import org.switchyard.SwitchYardException;
import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.security.BaseSecurityLogger;

/**
 * PrivateCrypto.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class PrivateCrypto {

    private static final String FORMAT = PrivateCrypto.class.getSimpleName() + "@%s[sealAlgorithm=%s, secretKey=%s]";

    /** The sealAlgorithm property. */
    public static final String SEAL_ALGORITHM = "sealAlgorithm";
    /** The sealKeySize property. */
    public static final String SEAL_KEY_SIZE = "sealKeySize";

    private String _sealAlgorithm = null;
    private SecretKey _secretKey = null;

    /**
     * Creates a new PrivateCrypto with the specified properties.
     * @param sealAlgorithm the sealAlgorithm
     * @param sealKeySize the sealKeySize
     */
    public PrivateCrypto(String sealAlgorithm, int sealKeySize) {
        init(sealAlgorithm, sealKeySize);
    }

    /**
     * Creates a new PrivateCrypto with the specified map.
     * @param map the map
     */
    public PrivateCrypto(Map<String, String> map) {
        this(new PropertiesPuller().pull(map));
    }

    /**
     * Creates a new PrivateCrypto with the specified properties.
     * @param properties the properties.
     */
    public PrivateCrypto(Properties properties) {
        String sealAlgorithm = Strings.trimToNull(properties.getProperty(SEAL_ALGORITHM));
        int sealKeySize = -1;
        String sealKeySizeProperty = Strings.trimToNull(properties.getProperty(SEAL_KEY_SIZE));
        if (sealKeySizeProperty != null) {
            try {
                sealKeySize = Integer.parseInt(sealKeySizeProperty);
            } catch (NumberFormatException nfe) {
                BaseSecurityLogger.ROOT_LOGGER.configurationNumberFormatException(SEAL_KEY_SIZE, sealKeySizeProperty, nfe);
            }
        }
        init(sealAlgorithm, sealKeySize);
    }

    private void init(String sealAlgorithm, int sealKeySize) {
        if (sealAlgorithm == null || sealKeySize < 1) {
            return;
        }
        _sealAlgorithm = sealAlgorithm;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(_sealAlgorithm);
            keyGenerator.init(sealKeySize);
            _secretKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException nsae) {
            throw new SwitchYardException(nsae);
        }
    }

    /**
     * Seals the specified object.
     * @param object the object
     * @return the sealed object
     */
    public SealedObject seal(Serializable object) {
        try {
            Cipher cipher = Cipher.getInstance(_sealAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, _secretKey);
            return new SealedObject(object, cipher);
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }

    /**
     * Unseals the specified sealed object.
     * @param object the sealed object
     * @return the unsealed object
     */
    public Serializable unseal(SealedObject object) {
        try {
            Cipher cipher = Cipher.getInstance(_sealAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, _secretKey);
            return (Serializable)object.getObject(cipher);
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(FORMAT, System.identityHashCode(this), _sealAlgorithm, _secretKey);
    }

}
