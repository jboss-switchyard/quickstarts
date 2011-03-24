/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.internal.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.switchyard.io.BaseSerializer;
import org.switchyard.io.CountingOutputStream;

/**
 * de/serializes objects using ObjectInputStream/ObjectOutputStream.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class ObjectStreamSerializer extends BaseSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out, int bufferSize) throws IOException {
        out = new CountingOutputStream(new BufferedOutputStream(out, bufferSize));
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(out);
            oos.writeObject(obj);
            oos.flush();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Throwable t) {
                // minimum one statement just to keep checkstyle happy
                t.getMessage();
            }
        }
        return ((CountingOutputStream)out).getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream in, Class<T> type, int bufferSize) throws IOException {
        in = new BufferedInputStream(in, bufferSize);
        ObjectInputStream ois = null;
        Object obj;
        try {
            ois = new ObjectInputStream(in);
            obj = ois.readObject();
        } catch (ClassNotFoundException cnfe) {
            throw new IOException(cnfe);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (Throwable t) {
                // minimum one statement just to keep checkstyle happy
                t.getMessage();
            }
        }
        return type.cast(obj);
    }

}
