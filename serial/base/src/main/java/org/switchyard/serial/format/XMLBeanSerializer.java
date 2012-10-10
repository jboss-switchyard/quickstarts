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
package org.switchyard.serial.format;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.switchyard.common.io.CountingOutputStream;
import org.switchyard.common.type.Classes;
import org.switchyard.serial.BaseSerializer;
import org.switchyard.serial.FormatType;

/**
 * A JDK serializer that performs {@link FormatType.XML_BEAN} serialization/deserialization.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class XMLBeanSerializer extends BaseSerializer {

    /**
     * Default constructor.
     */
    public XMLBeanSerializer() {
        super(FormatType.XML_BEAN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> int serialize(T obj, Class<T> type, OutputStream out) throws IOException {
        out = new CountingOutputStream(new BufferedOutputStream(out, getBufferSize()));
        EL el = new EL();
        XMLEncoder enc = new XMLEncoder(out);
        try {
            enc.setExceptionListener(el);
            enc.writeObject(obj);
            enc.flush();
        } finally {
            if (isCloseEnabled()) {
                enc.close();
            }
        }
        IOException ioe = el.getIOException();
        if (ioe != null) {
            throw ioe;
        }
        return ((CountingOutputStream)out).getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream in, Class<T> type) throws IOException {
        in = new BufferedInputStream(in, getBufferSize());
        EL el = new EL();
        XMLDecoder dec = new XMLDecoder(in, null, el, Classes.getTCCL());
        Object obj;
        try {
            obj = dec.readObject();
        } finally {
            if (isCloseEnabled()) {
                dec.close();
            }
        }
        IOException ioe = el.getIOException();
        if (ioe != null) {
            throw ioe;
        }
        return type.cast(obj);
    }

    private static final class EL implements ExceptionListener {

        private Exception _e;

        /**
         * {@inheritDoc}
         */
        @Override
        public void exceptionThrown(Exception e) {
            if (_e != null) {
                _e = e;
            }
        }

        /**
         * Gets any problem that occurred as an IOException.
         * @return the problem
         */
        public IOException getIOException() {
            if (_e != null) {
                if (_e instanceof IOException) {
                    return (IOException)_e;
                }
                return new IOException(_e);
            }
            return null;
        }

    }

}
