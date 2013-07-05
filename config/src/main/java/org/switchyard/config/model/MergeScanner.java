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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Scanner} that merges all {@link Model}s from other Scanners into one.
 *
 * @param <M> the Model type to scan for (and merge)
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class MergeScanner<M extends Model> implements Scanner<M> {

    private final Class<M> _clazz;
    private final boolean _fromOverridesTo;
    private final List<Scanner<M>> _scanners;

    /**
     * Constructs a new MergeScanner using the specified parameters.
     * @param clazz the type of Model that will be scanned for and merged
     * @param fromOverridesTo whether or not each successfully merged Model's values will override the next Model to merge values
     * @param scanners the Scanners to merge output from
     */
    public MergeScanner(Class<M> clazz, boolean fromOverridesTo, Scanner<M>... scanners) {
        _clazz = clazz;
        _fromOverridesTo = fromOverridesTo;
        List<Scanner<M>> list = new ArrayList<Scanner<M>>();
        if (scanners != null) {
            for (Scanner<M> scanner : scanners) {
                if (scanner != null) {
                    list.add(scanner);
                }
            }
        }
        _scanners = list;
    }

    /**
     * Constructs a new MergeScanner using the specified parameters.
     * @param clazz the type of Model that will be scanned for and merged
     * @param fromOverridesTo whether or not each successfully merged Model's values will override the next Model to merge values
     * @param scanners the Scanners to merge output from
     */
    public MergeScanner(Class<M> clazz, boolean fromOverridesTo, List<Scanner<M>> scanners) {
        _clazz = clazz;
        _fromOverridesTo = fromOverridesTo;
        _scanners = new ArrayList<Scanner<M>>();
        if (scanners != null) {
            for (Scanner<M> scanner : scanners) {
                if (scanner != null) {
                    _scanners.add(scanner);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<M> scan(ScannerInput<M> input) throws IOException {
        M merged;
        try {
            merged = _clazz.newInstance();
        } catch (Exception e) {
            throw new IOException(e);
        }
        for (Scanner<M> scanner : _scanners) {
            ScannerOutput<M> scannerOutput = scanner.scan(input);
            if (scannerOutput != null) {
                List<M> scanned_list = scannerOutput.getModels();
                if (scanned_list != null) {
                    for (M scanned : scanned_list) {
                        if (scanned != null) {
                            merged = Models.merge(scanned, merged, _fromOverridesTo);
                        }
                    }
                }
            }
        }
        return new ScannerOutput<M>().setModel(merged);
    }

}
