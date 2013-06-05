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
