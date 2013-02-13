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
 * BaseProject.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public class BaseProject implements Project {

    private final String _groupId;
    private final String _artifactId;
    private final String _packaging;
    private final String _name;
    private final String _description;
    private final URL _url;
    // we maintain a URI internally for equals() and hashCode() since URLs perform name resolution during equals()
    private final URI _uri;
    private final String _version;

    /**
     * Constructs a new BaseSpecification.
     * @param groupId the groupId
     * @param artifactId the artifactId
     * @param packaging the packaging
     * @param name the name
     * @param description the description
     * @param url the url
     * @param version the version
     */
    public BaseProject(String groupId, String artifactId, String packaging, String name, String description, URL url, String version) {
        _groupId = groupId;
        _artifactId = artifactId;
        _packaging = packaging;
        _name = name;
        _description = description;
        _url = url;
        _uri = toURI(_url);
        _version = version;
    }

    /**
     * Constructs a new BaseSpecification.
     * @param groupId the groupId
     * @param artifactId the artifactId
     * @param packaging the packaging
     * @param name the name
     * @param description the description
     * @param url the url
     * @param version the version
     */
    public BaseProject(String groupId, String artifactId, String packaging, String name, String description, String url, String version) {
        _groupId = groupId;
        _artifactId = artifactId;
        _packaging = packaging;
        _name = name;
        _description = description;
        _url = toURL(url);
        _uri = toURI(_url);
        _version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGroupId() {
        return _groupId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getArtifactId() {
        return _artifactId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackaging() {
        return _packaging;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return _description;
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
        return String.format("%s [groupId=%s, artifactId=%s, packaging=%s, name=%s, description=%s, url=%s, version=%s]", Project.class.getSimpleName(), getGroupId(), getArtifactId(), getPackaging(), getName(), getDescription(), getURL(), getVersion());
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
        BaseProject other = (BaseProject)obj;
        if (_groupId == null) {
            if (other._groupId != null) {
                return false;
            }
        } else if (!_groupId.equals(other._groupId)) {
            return false;
        }
        if (_artifactId == null) {
            if (other._artifactId != null) {
                return false;
            }
        } else if (!_artifactId.equals(other._artifactId)) {
            return false;
        }
        if (_packaging == null) {
            if (other._packaging != null) {
                return false;
            }
        } else if (!_packaging.equals(other._packaging)) {
            return false;
        }
        if (_name == null) {
            if (other._name != null) {
                return false;
            }
        } else if (!_name.equals(other._name)) {
            return false;
        }
        if (_description == null) {
            if (other._description != null) {
                return false;
            }
        } else if (!_description.equals(other._description)) {
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
        result = prime * result + (_groupId == null ? 0 : _groupId.hashCode());
        result = prime * result + (_artifactId == null ? 0 : _artifactId.hashCode());
        result = prime * result + (_packaging == null ? 0 : _packaging.hashCode());
        result = prime * result + (_name == null ? 0 : _name.hashCode());
        result = prime * result + (_description == null ? 0 : _description.hashCode());
        result = prime * result + (_uri == null ? 0 : _uri.hashCode());
        result = prime * result + (_version == null ? 0 : _version.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Project that) {
        int comparison = 0;
        if (this != that) {
            comparison = compare(_groupId, that.getGroupId());
            if (comparison == 0) {
                comparison = compare(_artifactId, that.getArtifactId());
                if (comparison == 0) {
                    comparison = compare(_packaging, that.getPackaging());
                    if (comparison == 0) {
                        comparison = compare(_name, that.getName());
                        if (comparison == 0) {
                            comparison = compare(_description, that.getDescription());
                            if (comparison == 0) {
                                comparison = compare(toString(_url), toString(that.getURL()));
                                if (comparison == 0) {
                                    comparison = compare(_version, that.getVersion());
                                }
                            }
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
