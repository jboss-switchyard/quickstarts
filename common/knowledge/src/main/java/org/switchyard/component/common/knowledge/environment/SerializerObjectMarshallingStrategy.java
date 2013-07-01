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
package org.switchyard.component.common.knowledge.environment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.switchyard.serial.Serializer;

/**
 * SerializerObjectMarshallingStrategy.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class SerializerObjectMarshallingStrategy implements ObjectMarshallingStrategy {

    private static final String[] IGNORE_PKGS = new String[] {
        "java.", "javax.", "org.w3c.dom.", "org.jbpm.", "org.drools.", "org.kie."
    };

    private final Serializer _serializer;

    /**
     * Constructs a new SerializerObjectMarshallingStrategy with the specified Serializer.
     * @param serializer the specified Serializer
     */
    public SerializerObjectMarshallingStrategy(Serializer serializer) {
        _serializer = serializer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(Object object) {
        if (object != null) {
            String cn = object.getClass().getName();
            for (String ip : IGNORE_PKGS) {
                // TODO: Replace with something more elegant? Perhaps check the classloader?
                if (cn.startsWith(ip)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(ObjectOutputStream os, Object object) throws IOException {
        os.writeObject(_serializer.serialize(object, Object.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object read(ObjectInputStream os) throws IOException, ClassNotFoundException {
        return _serializer.deserialize((byte[])os.readObject(), Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] marshal(Context context, ObjectOutputStream os, Object object) throws IOException {
        return _serializer.serialize(object, Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object unmarshal(Context context, ObjectInputStream is, byte[] object, ClassLoader classloader) throws IOException, ClassNotFoundException {
        return _serializer.deserialize(object, Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context createContext() {
        return null;
    }

}
