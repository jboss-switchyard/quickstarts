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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ScannerOutput.
 *
 * @param <M> the Model type being scanned for
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ScannerOutput<M extends Model> {

    private List<M> _models;

    public ScannerOutput() {
        _models = new ArrayList<M>();
    }

    public synchronized M getModel() {
        return _models.size() > 0 ? _models.get(0) : null;
    }

    public synchronized ScannerOutput<M> setModel(M model) {
        return setModels(Collections.singletonList(model));
    }

    public synchronized List<M> getModels() {
        return Collections.unmodifiableList(_models);
    }

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
