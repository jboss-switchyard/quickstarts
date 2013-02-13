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
package org.switchyard.common.version.manifest;

import static java.util.jar.Attributes.Name.IMPLEMENTATION_TITLE;
import static java.util.jar.Attributes.Name.IMPLEMENTATION_URL;
import static java.util.jar.Attributes.Name.IMPLEMENTATION_VENDOR;
import static java.util.jar.Attributes.Name.IMPLEMENTATION_VENDOR_ID;
import static java.util.jar.Attributes.Name.IMPLEMENTATION_VERSION;
import static org.switchyard.common.version.manifest.ManifestVersion.getMainAttributesValue;

import java.util.jar.Manifest;

import org.switchyard.common.version.BaseImplementation;

/**
 * ManifestImplementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
final class ManifestImplementation extends BaseImplementation {
    
    ManifestImplementation(Manifest manifest) {
        super(getMainAttributesValue(manifest, IMPLEMENTATION_TITLE),
              getMainAttributesValue(manifest, IMPLEMENTATION_VENDOR),
              getMainAttributesValue(manifest, IMPLEMENTATION_VENDOR_ID),
              getMainAttributesValue(manifest, IMPLEMENTATION_URL),
              getMainAttributesValue(manifest, IMPLEMENTATION_VERSION));
    }

}
