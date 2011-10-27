/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.validate.config.model.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.validate.config.model.JavaValidateModel;
import org.switchyard.validate.config.model.XmlValidateModel;

/**
 * Marshalls validate Models.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class V1ValidateMarshaller extends BaseMarshaller {

    private static final String VALIDATE_JAVA = ValidateModel.VALIDATE + "." + JavaValidateModel.JAVA;
    private static final String VALIDATE_XML = ValidateModel.VALIDATE + "." + XmlValidateModel.XML;

    /**
     * Constructs a new V1ValidateMarshaller with the specified Descriptor.
     * @param desc the Descriptor
     */
    public V1ValidateMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();

        if (name.equals(VALIDATE_JAVA)) {
            return new V1JavaValidateModel(config, desc);
        } else if (name.equals(VALIDATE_XML)) {
            return new V1XmlValidateModel(config, desc);
        }

        return null;
    }

}
