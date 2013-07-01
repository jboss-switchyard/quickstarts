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
 
package org.switchyard.component.http;

import java.nio.charset.Charset;

/**
 * Content Type place holder.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class ContentType {

    private static final String DEFAULT_MIME_TYPE = "text/plain";
    private static final String CHARSET = "charset";

    private String _mimeType;
    private String _charset;

    /**
     * Construct a default content type.
     */
    public ContentType() {
        _mimeType = DEFAULT_MIME_TYPE;
        _charset = Charset.defaultCharset().name();
    }

    /**
     * Construct a content type with a Content-Type string.
     * @param contentType the Content-Type string
     */
    public ContentType(String contentType) {
        if (contentType == null) {
            _mimeType = DEFAULT_MIME_TYPE;
            _charset = Charset.defaultCharset().name();
        } else {
            String[] types = contentType.split(";");
            _mimeType = types[0];
            for (int i = 1; i < types.length; i++) {
                if (types[i].contains(CHARSET)) {
                    _charset = types[i].split("=")[1];
                }
            }
        }
    }

    /**
     * Get the mime type.
     * @return the MimeType
     */
    public String getMimeType() {
        return _mimeType;
    }

    /**
     * Set the mime type.
     * @param mimeType the mime type to set
     */
    public void setMimeType(String mimeType) {
        _mimeType = mimeType;
    }

    /**
     * Get the character encoding.
     * @return the charset
     */
    public String getCharset() {
        return _charset;
    }

    /**
     * Set the character encoding.
     * @param charset the character encoding to set
     */
    public void setCharset(String charset) {
        _charset = charset;
    }

    @Override
    public String toString() {
        return _mimeType + ";" + CHARSET + "=" + _charset;
    }
}
