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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Cipher;

import org.switchyard.SwitchYardException;
import org.switchyard.common.io.Buffers;
import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.io.pull.Puller.PathType;
import org.switchyard.common.lang.Strings;
import org.switchyard.security.pull.KeyStorePuller;

/**
 * PublicCrypto.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class PublicCrypto {

    private static final String FORMAT = PublicCrypto.class.getSimpleName() + "@%s[key=%s, publicKey=%s, keyTransformation=%s]";

    /** The keyStoreLocation property. */
    public static final String KEYSTORE_LOCATION = "keyStoreLocation";
    /** The keyStoreType property. */
    public static final String KEYSTORE_TYPE = "keyStoreType";
    /** The keyStorePassword property. */
    public static final String KEYSTORE_PASSWORD = "keyStorePassword";
    /** The keyAlias property. */
    public static final String KEY_ALIAS = "keyAlias";
    /** The keyPassword property. */
    public static final String KEY_PASSWORD = "keyPassword";
    /** The keyTransformation property. */
    public static final String KEY_TRANSFORMATION = "keyTransformation";

    private Key _key = null;
    private PublicKey _publicKey = null;
    private String _keyTransformation = null;

    /**
     * Creates a new PublicCrypto with the specified properties.
     * @param keyStoreLocation the keyStoreLocation
     * @param keyStoreType the keyStoreType
     * @param keyStorePassword the keyStorePassword
     * @param keyAlias the keyAlias
     * @param keyPassword the keyPassword
     * @param keyTransformation the keyTransformation
     */
    public PublicCrypto(String keyStoreLocation, String keyStoreType, char[] keyStorePassword, String keyAlias, char[] keyPassword, String keyTransformation) {
        init(keyStoreLocation, keyStoreType, keyStorePassword, keyAlias, keyPassword, keyTransformation);
    }

    /**
     * Creates a new PublicCrypto with the specified map.
     * @param map the map
     */
    public PublicCrypto(Map<String, String> map) {
        this(new PropertiesPuller().pull(map));
    }

    /**
     * Creates a new PublicCrypto with the specified properties.
     * @param properties the properties.
     */
    public PublicCrypto(Properties properties) {
        String keyStoreLocation = Strings.trimToNull(properties.getProperty(KEYSTORE_LOCATION));
        String keyStoreType = Strings.trimToNull(properties.getProperty(KEYSTORE_TYPE));
        String keyStorePasswordProperty = properties.getProperty(KEYSTORE_PASSWORD);
        char[] keyStorePassword = keyStorePasswordProperty != null ? keyStorePasswordProperty.toCharArray() : null;
        String keyAlias = Strings.trimToNull(properties.getProperty(KEY_ALIAS));
        String keyPasswordProperty = properties.getProperty(KEY_PASSWORD);
        char[] keyPassword = keyPasswordProperty != null ? keyPasswordProperty.toCharArray() : null;
        String keyTransformation = Strings.trimToNull(properties.getProperty(KEY_TRANSFORMATION));
        init(keyStoreLocation, keyStoreType, keyStorePassword, keyAlias, keyPassword, keyTransformation);
    }

    private void init(String keyStoreLocation, String keyStoreType, char[] keyStorePassword, String keyAlias, char[] keyPassword, String keyTransformation) {
        if (keyStoreLocation == null) {
            return;
        }
        KeyStorePuller keyStorePuller = new KeyStorePuller(keyStoreType, keyStorePassword);
        KeyStore keyStore = keyStorePuller.pullPath(keyStoreLocation, getClass(), PathType.values());
        try {
            Certificate certificate = keyStore.getCertificate(keyAlias);
            _publicKey = certificate.getPublicKey();
            _keyTransformation = keyTransformation != null ? keyTransformation : _publicKey.getAlgorithm();
        } catch (KeyStoreException kse) {
            throw new SwitchYardException(kse);
        }
    }

    /**
     * Encrypts the specified object.
     * @param object the object
     * @return the encrypted bytes
     */
    public byte[] encrypt(Serializable object) {
        try {
            ByteArrayOutputStream encOut = new ByteArrayOutputStream();
            ByteArrayInputStream objIn = new ByteArrayInputStream(toBytes(object));
            byte[] buf = Buffers.newDefaultBuffer();
            int bufLength;
            Cipher cipher = Cipher.getInstance(_keyTransformation);
            cipher.init(Cipher.ENCRYPT_MODE, _publicKey);
            while ((bufLength = objIn.read(buf)) != -1) {
                byte[] encBytes = cipher.doFinal(copyBytes(buf, bufLength));
                encOut.write(encBytes);
            }
            encOut.flush();
            return encOut.toByteArray();
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }

    /**
     * Decrypts the specified encrypted bytes.
     * @param bytes the encrypted bytes
     * @return the decrypted object
     */
    public Serializable decrypt(byte[] bytes) {
        try {
            ByteArrayInputStream encIn = new ByteArrayInputStream(bytes);
            ByteArrayOutputStream decOut = new ByteArrayOutputStream();
            byte[] buf = Buffers.newDefaultBuffer();
            int bufLength;
            Cipher cipher = Cipher.getInstance(_keyTransformation);
            cipher.init(Cipher.DECRYPT_MODE, _key);
            while ((bufLength = encIn.read(buf)) != -1) {
                byte[] decBytes = cipher.doFinal(copyBytes(buf, bufLength));
                decOut.write(decBytes);
            }
            decOut.flush();
            return toSerializable(decOut.toByteArray());
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }

    private byte[] copyBytes(byte[] src, int length) {
        if (src.length == length) {
            return src;
        }
        byte[] dest = new byte[length];
        System.arraycopy(src, 0, dest, 0, length);
        return dest;
    }

    private byte[] toBytes(Serializable object) throws IOException {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
            return baos.toByteArray();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (Throwable t) {
                    t.getMessage();
                }
            }
        }
    }

    private Serializable toSerializable(byte[] bytes) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (Serializable)ois.readObject();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (Throwable t) {
                    t.getMessage();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(FORMAT, System.identityHashCode(this), _key, _publicKey, _keyTransformation);
    }

}
