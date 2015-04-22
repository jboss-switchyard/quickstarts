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
 
package org.switchyard.component.http.composer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.switchyard.component.common.composer.BindingData;
import org.switchyard.component.http.ContentType;

/**
 * HTTP binding data that wraps the headers and body.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> &copy; 2012 Red Hat Inc.
 */
public abstract class HttpBindingData implements BindingData {

    private Map<String, List<String>> _headers;
    private byte[] _body;
    private ContentType _contentType;

    /**
     * Get the HTTP headers map.
     * @return a Map of headers
     */
    public Map<String, List<String>> getHeaders() {
        if (_headers == null) {
            _headers = new HashMap<String, List<String>>();
        }
        return _headers;
    }

    /**
     * Set the HTTP headers map.
     * @param headers a Map of headers
     */
    public void setHeaders(Map<String, List<String>> headers) {
        _headers = headers;
    }

    /**
     * Add a HTTP header.
     * @param name the name of the header
     * @param values a List of header values
     */
    public void addHeader(String name, List<String> values) {
        if (_headers == null) {
            _headers = new HashMap<String, List<String>>();
        }
        _headers.put(name, values);
    }

    /**
     * Add a HTTP header.
     * @param name the name of the header
     * @param value a header value
     */
    public void addHeader(String name, String value) {
        if (_headers == null) {
            _headers = new HashMap<String, List<String>>();
        }
        List<String> values = _headers.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            _headers.put(name, values);
        }
        values.add(value);
    }

    /**
     * Get the HTTP body.
     * @return the body as a StringReader
     * @throws UnsupportedEncodingException if content encoding is not supported
     */
    public StringReader getBody() throws UnsupportedEncodingException {
        String body = null;
        if ((_contentType != null) && (_contentType.getCharset() != null)) {
            body = new String(_body, _contentType.getCharset());
        } else {
            body = new String(_body);
        }
        return new StringReader(body);
    }

    /**
     * Get the HTTP body.
     * @return the body as a String
     * @throws UnsupportedEncodingException if content encoding is not supported
     */
    public String getBodyAsString() throws UnsupportedEncodingException {
        String body = null;
        if ((_contentType != null) && (_contentType.getCharset() != null)) {
            body = new String(_body, _contentType.getCharset());
        } else {
            body = new String(_body);
        }
        return body;
    }

    /**
     * Get the HTTP body as byte array.
     * @return the body
     */
    public ByteArrayInputStream getBodyBytes() {
        if (_body != null) {
            return new ByteArrayInputStream(_body);
        } else {
            return null;
        }
    }

    /**
     * Set the HTTP body using a stream.
     * @param is the body as InputStream
     * @throws IOException if content could not be read
     */
    public void setBodyFromStream(InputStream is) throws IOException {
        if (is != null) {
            ByteArrayOutputStream tmp = new ByteArrayOutputStream();
            byte[] buff = new byte[512];
            int buffSize = 0;
            while ((buffSize = is.read(buff)) >= 0) {
                tmp.write(buff, 0, buffSize);
            }
            _body = tmp.toByteArray();
            is.close();
        } else {
            _body = null;
        }
    }

    /**
     * Set the HTTP body using a reader.
     * @param reader the body as Reader
     * @throws IOException if content could not be read
     */
    public void setBodyFromReader(Reader reader) throws IOException {
        if (reader != null) {
            StringWriter tmp = new StringWriter();
            char[] buff = new char[512];
            int buffSize = 0;
            while ((buffSize = reader.read(buff)) >= 0) {
                tmp.write(buff, 0, buffSize);
            }
            _body = tmp.toString().getBytes();
            reader.close();
        } else {
            _body = null;
        }
    }

    /**
     * Writes the HTTP body to a stream.
     * @param os an OutputStream to write to
     * @throws IOException if content could not be written
     */
    public void writeBodyToStream(OutputStream os) throws IOException {
        if (os != null) {
            os.write(_body);
            os.close();
        }
    }

    /**
     * Set the HTTP body.
     * @param body the body as String
     */
    public void setBody(String body) {
        if (_contentType != null) {
            _body = body.getBytes(Charset.forName(_contentType.getCharset()));
        } else {
            _body = body.getBytes();
        }
    }

    /**
     * Set the HTTP body.
     * @param body the body as byte array
     */
    public void setBodyBytes(byte[] body) {
        if (body != null) {
            _body = body.clone();
        } else {
            _body = null;
        }
    }

    /**
     * Get the HTTP body's content type.
     * @return the content type
     */
    public ContentType getContentType() {
        return _contentType;
    }

    /**
     * Set the HTTP body's content type.
     * @param contentType the content type
     */
    public void setContentType(ContentType contentType) {
        _contentType = contentType;
    }
}
