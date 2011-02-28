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
package org.switchyard.config.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The input to a {@link Scanner}.
 *
 * @param <M> the Model type being scanned for
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ScannerInput<M extends Model> {

    private List<URL> _urls;
    private String _name;

    /**
     * Constructs a new ScannerInput.
     */
    public ScannerInput() {
        _urls = new ArrayList<URL>();
    }

    /**
     * Gets the URLs to scan.
     * @return the URLs
     */
    public synchronized List<URL> getURLs() {
        return Collections.unmodifiableList(_urls);
    }

    /**
     * Sets the URLs to scan.
     * @param urls the URLs
     * @return this ScannerInput (useful for chaining)
     */
    public synchronized ScannerInput<M> setURLs(List<URL> urls) {
        _urls.clear();
        if (urls != null) {
            for (URL url : urls) {
                if (url != null) {
                    _urls.add(url);
                }
            }
        }
        return this;
    }

    /**
     * Gets a contextual name for the scan.
     * @return the contextual name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets a contextual name for the scan.
     * @param name the contextual name
     * @return this ScannerInput (useful for chaining)
     */
    public ScannerInput<M> setName(String name) {
        _name = name;
        return this;
    }

}
