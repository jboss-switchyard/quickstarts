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
