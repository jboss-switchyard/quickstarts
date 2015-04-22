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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The output of a {@link Scanner}.
 *
 * @param <M> the Model type being scanned for
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ScannerOutput<M extends Model> {

    private List<M> _models;

    /**
     * Constructs a new ScannerOutput.
     */
    public ScannerOutput() {
        _models = new ArrayList<M>();
    }

    /**
     * Gets the first (and possibly only) Model found/created by the scan.
     * @return the Model
     */
    public synchronized M getModel() {
        return _models.size() > 0 ? _models.get(0) : null;
    }

    /**
     * Sets the first (and possibly only) Model found/created by the scan.
     * @param model the Model
     * @return this ScannerOutput (useful for chaining)
     */
    public synchronized ScannerOutput<M> setModel(M model) {
        return setModels(Collections.singletonList(model));
    }

    /**
     * Gets the Models found/created by the scan.
     * @return the Models
     */
    public synchronized List<M> getModels() {
        return Collections.unmodifiableList(_models);
    }

    /**
     * Sets the Models found/created by the scan.
     * @param models the Models
     * @return this ScannerOutput (useful for chaining)
     */
    public synchronized ScannerOutput<M> setModels(List<M> models) {
        _models.clear();
        if (models != null) {
            for (M m : models) {
                if (m != null) {
                    _models.add(m);
                }
            }
        }
        return this;
    }

}
