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
package org.switchyard.config.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.switchyard.config.model.switchyard.SwitchYardNamespace;

/**
 * The input to a {@link Scanner}.
 *
 * @param <M> the Model type being scanned for
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ScannerInput<M extends Model> {

    private List<URL> _urls;
    private SwitchYardNamespace _switchyardNamespace;
    private String _compositeName;
    private List<Package> _includes;
    private List<Package> _excludes;

    /**
     * Constructs a new ScannerInput.
     */
    public ScannerInput() {
        _urls = new ArrayList<URL>();
        _includes = new ArrayList<Package>();
        _excludes = new ArrayList<Package>();
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
     * Gets the Packages to include.
     * @return the Packages
     */
    public synchronized List<Package> getIncludePackages() {
        return Collections.unmodifiableList(_includes);
    }

    /**
     * Sets the Packages to include.
     * @param includes packages to include
     * @return this ScannerInput (useful for chaining)
     */
    public synchronized ScannerInput<M> setIncludePackages(List<Package> includes) {
        _includes.clear();
        if (includes != null) {
            for (Package p : includes) {
                if (p != null) {
                    _includes.add(p);
                }
            }
        }
        return this;
    }

    /**
     * Gets the Packages to exclude.
     * @return the Packages
     */
    public synchronized List<Package> getExcludePackages() {
        return Collections.unmodifiableList(_excludes);
    }

    /**
     * Sets the Packages to exclude.
     * @param excludes packages to exclude
     * @return this ScannerInput (useful for chaining)
     */
    public synchronized ScannerInput<M> setExcludePackages(List<Package> excludes) {
        _excludes.clear();
        if (excludes != null) {
            for (Package p : excludes) {
                if (p != null) {
                    _excludes.add(p);
                }
            }
        }
        return this;
    }

    /**
     * If the switchyard namespace is set.
     * @return if the switchyard namespace is set
     */
    public boolean isSwitchyardNamespaceSet() {
        return _switchyardNamespace != null;
    }

    /**
     * Gets the switchyard namespace for the scan.
     * @return the switchyard namespace
     */
    public SwitchYardNamespace getSwitchyardNamespace() {
        return isSwitchyardNamespaceSet() ? _switchyardNamespace : SwitchYardNamespace.DEFAULT;
    }

    /**
     * Sets the switchyard namespace for the scan.
     * @param switchyardNamespace the switchyard namespace
     * @return this ScannerInput (useful for chaining)
     */
    public ScannerInput<M> setSwitchyardNamespace(SwitchYardNamespace switchyardNamespace) {
        _switchyardNamespace = switchyardNamespace;
        return this;
    }

    /**
     * Gets the composite name for the scan.
     * @return the composite name
     */
    public String getCompositeName() {
        return _compositeName;
    }

    /**
     * Sets the composite name for the scan.
     * @param compositeName the composite name
     * @return this ScannerInput (useful for chaining)
     */
    public ScannerInput<M> setCompositeName(String compositeName) {
        _compositeName = compositeName;
        return this;
    }
    
}
