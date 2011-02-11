/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.config.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Resource.
 * 
 * @param <R> the type of the resource being pulled
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class Resource<R> {

    public R pull(String resource) throws IOException {
        InputStream is = Classes.getResourceAsStream(resource, getClass());
        if (is != null) {
            try {
                return pull(is);
            } finally {
                is.close();
            }
        }
        return null;
    }

    public R pull(URI uri) throws IOException {
        return pull(uri.toURL());
    }

    public R pull(URL url) throws IOException {
        InputStream is = url.openStream();
        try {
            return pull(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public R pull(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        try {
            return pull(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public abstract R pull(InputStream is) throws IOException;

}
