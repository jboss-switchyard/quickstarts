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

    private SwitchYardScanner[] _scanners;

    public MergeSwitchYardScanner() {
        _scanners = new SwitchYardScanner[0];
    }

    public MergeSwitchYardScanner(SwitchYardScanner... scanners) {
        int length = scanners.length;
        SwitchYardScanner[] copy = new SwitchYardScanner[length];
        System.arraycopy(scanners, 0, copy, 0, length);
        _scanners = copy;
    }

    public MergeSwitchYardScanner(List<SwitchYardScanner> scanners) {
        SwitchYardScanner[] copy = new SwitchYardScanner[scanners.size()];
        _scanners = scanners.toArray(copy);
    }

    public SwitchYardModel scan(Set<String> paths) throws IOException {
        SwitchYardModel merged = new V1SwitchYardModel();
        for (SwitchYardScanner scanner : _scanners) {
            SwitchYardModel scanned = scanner.scan(paths);
            if (scanned != null) {
                merged = (SwitchYardModel)Models.merge(scanned, merged);
            }
        }
        return merged;
    }

}
