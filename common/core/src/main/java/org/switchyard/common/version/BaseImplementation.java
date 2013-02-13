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
package org.switchyard.common.version;

import static org.switchyard.common.version.BaseVersion.compare;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * BaseImplementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public class BaseImplementation implements Implementation {

    private final String _title;
    private final String _vendor;
    private final String _vendorId;
    private final URL _url;
    // we maintain a URI internally for equals() and hashCode() since URLs perform name resolution during equals()
    private final URI _uri;
    private final String _version;

    /**
     * Constructs a new BaseImplementation.
     * @param title the title
     * @param vendor the vendor
     * @param vendorId the vendorId
     * @param url the url
     * @param version the version
     */
    public BaseImplementation(String title, String vendor, String vendorId, URL url, String version) {
        _title = title;
        _vendor = vendor;
        _vendorId = vendorId;
        _url = url;
        _uri = toURI(_url);
        _version = version;
    }

    /**
     * Constructs a new BaseImplementation.
     * @param title the title
     * @param vendor the vendor
     * @param vendorId the vendorId
     * @param url the url
     * @param version the version
     */
    public BaseImplementation(String title, String vendor, String vendorId, String url, String version) {
        _title = title;
        _vendor = vendor;
        _vendorId = vendorId;
        _url = toURL(url);
        _uri = toURI(_url);
        _version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return _title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVendor() {
        return _vendor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVendorId() {
        return _vendorId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getURL() {
        return _url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion() {
        return _version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s [title=%s, vendor=%s, vendorId=%s, url=%s, version=%s]", Implementation.class.getSimpleName(), getTitle(), getVendor(), getVendorId(), getURL(), getVersion());
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseImplementation other = (BaseImplementation)obj;
        if (_title == null) {
            if (other._title != null) {
                return false;
            }
        } else if (!_title.equals(other._title)) {
            return false;
        }
        if (_vendor == null) {
            if (other._vendor != null) {
                return false;
            }
        } else if (!_vendor.equals(other._vendor)) {
            return false;
        }
        if (_vendorId == null) {
            if (other._vendorId != null) {
                return false;
            }
        } else if (!_vendorId.equals(other._vendorId)) {
            return false;
        }
        if (_uri == null) {
            if (other._uri != null) {
                return false;
            }
        } else if (!_uri.equals(other._uri)) {
            return false;
        }
        if (_version == null) {
            if (other._version != null) {
                return false;
            }
        } else if (!_version.equals(other._version)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_title == null ? 0 : _title.hashCode());
        result = prime * result + (_vendor == null ? 0 : _vendor.hashCode());
        result = prime * result + (_vendorId == null ? 0 : _vendorId.hashCode());
        result = prime * result + (_uri == null ? 0 : _uri.hashCode());
        result = prime * result + (_version == null ? 0 : _version.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Implementation that) {
        int comparison = 0;
        if (this != that) {
            comparison = compare(_title, that.getTitle());
            if (comparison == 0) {
                comparison = compare(_vendor, that.getVendor());
                if (comparison == 0) {
                    comparison = compare(_vendorId, that.getVendorId());
                    if (comparison == 0) {
                        comparison = compare(toString(_url), toString(that.getURL()));
                        if (comparison == 0) {
                            comparison = compare(_version, that.getVersion());
                        }
                    }
                }
            }
        }
        return comparison;
    }

    private URI toURI(URL url) {
        if (url != null) {
            try {
                return url.toURI();
            } catch (URISyntaxException usi) {
                return null;
            }
        }
        return null;
    }

    private URL toURL(String url) {
        if (url != null) {
            try {
                return new URL(url);
            } catch (MalformedURLException mue) {
                return null;
            }
        }
        return null;
    }

    private String toString(URL url) {
        return url != null ? url.toString() : null;
    }

}
