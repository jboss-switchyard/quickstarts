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
package org.switchyard.config.model.switchyard;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.switchyard.config.model.Models;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;

/**
 * MergeSwitchYardScanner.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class MergeSwitchYardScanner implements SwitchYardScanner {

    private boolean _fromOverridesTo;
    private SwitchYardScanner[] _scanners;

    public MergeSwitchYardScanner(boolean fromOverridesTo, SwitchYardScanner... scanners) {
        _fromOverridesTo = fromOverridesTo;
        int length = scanners.length;
        SwitchYardScanner[] copy = new SwitchYardScanner[length];
        System.arraycopy(scanners, 0, copy, 0, length);
        _scanners = copy;
    }

    public MergeSwitchYardScanner(boolean fromOverridesTo, List<SwitchYardScanner> scanners) {
        _fromOverridesTo = fromOverridesTo;
        SwitchYardScanner[] copy = new SwitchYardScanner[scanners.size()];
        _scanners = scanners.toArray(copy);
    }

    public Collection<SwitchYardModel> scan(Set<String> paths) throws IOException {
        return Collections.singletonList(merge(paths));
    }

    public SwitchYardModel merge(Set<String> paths) throws IOException {
        SwitchYardModel merged = new V1SwitchYardModel();
        for (SwitchYardScanner scanner : _scanners) {
            Collection<SwitchYardModel> scanned_coll = scanner.scan(paths);
            if (scanned_coll != null) {
                for (SwitchYardModel scanned : scanned_coll) {
                    if (scanned != null) {
                        merged = (SwitchYardModel)Models.merge(scanned, merged, _fromOverridesTo);
                    }
                }
            }
        }
        return merged;
    }

}
