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
package org.switchyard.component.jca.processor.cci;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.resource.cci.Record;
import javax.resource.cci.Streamable;

/**
 * Record for raw byte content.
 *
 * @author Antti Laisi
 */
public class StreamableRecord implements Streamable, Record {

    private static final long serialVersionUID = 1L;

    private String _recordName;
    private String _recordShortDescription;

    private byte[] _bytes;

    @Override
    public String getRecordName() {
        return _recordName;
    }

    @Override
    public String getRecordShortDescription() {
        return _recordShortDescription;
    }

    @Override
    public void setRecordName(String recordName) {
        _recordName = recordName; 
    }

    @Override
    public void setRecordShortDescription(String recordShortDescription) {
        _recordShortDescription = recordShortDescription;
    }

    @Override
    public void read(InputStream in) throws IOException {
        _bytes = new byte[in.available()];
        in.read(_bytes);
    }

    @Override
    public void write(OutputStream out) throws IOException {
        if (_bytes != null) {
            out.write(_bytes);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        StreamableRecord clone = new StreamableRecord();
        clone._recordName = _recordName;
        clone._recordShortDescription = _recordShortDescription;
        if (_bytes != null) {
            clone._bytes = Arrays.copyOf(_bytes, _bytes.length);
        }
        return clone;
    }

}
