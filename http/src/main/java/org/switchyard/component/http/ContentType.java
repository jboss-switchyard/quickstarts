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
}
