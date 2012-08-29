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
 
package org.switchyard.component.http.composer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
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
     * @return the body
     * @throws UnsupportedEncodingException if content encoding is not supported
     */
    public String getBody() throws UnsupportedEncodingException {
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
    public byte[] getBodyBytes() {
        if (_body != null) {
            return _body.clone();
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
