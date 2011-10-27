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

package org.switchyard.validate.config.model;

import org.switchyard.config.model.validate.ValidateModel;

/**
 * A "validate.xml" configuration model.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public interface XmlValidateModel extends ValidateModel {

    /** The "xml" name. */
    public static final String XML = "xml";
    
    /** schema file. */
    public static final String SCHEMA_FILE_URI = "schemaFile";

    /** schema language. */
    public static final String SCHEMA_TYPE = "schemaType";
    
    /** whether a warning should be reported as Exception or just log. */
    public static final String FAIL_ON_WARNING = "failOnWarning";
            
    /**
     * Return whether a warning should be reported as an SwitchYardException.
     * If failOnWarning attribute is "true", then a warning should be reported
     * as an SwitchYardException, otherwise just log.
     * @return true if a warning should be reported as an SwitchYardException, otherwise false
     */
    boolean failOnWarning();

    /**
     * Set whether a warning should be reported as an SwitchYardException.
     * If failOnWarning attribute is "true", then a warning should be reported
     * as an SwitchYardException, otherwise just log.
     * @param failOnWarning true if a warning should be reported as an SwitchYardException, otherwise false
     * @return model representation
     */
    XmlValidateModel setFailOnWarning(boolean failOnWarning);
    
    /**
     * @return schema file
     */
    String getSchemaFile();

    /**
     * @param file schema file
     * @return model representation
     */
    XmlValidateModel setSchemaFile(String file);
    
    /**
     * @return schema type
     */
    XmlSchemaType getSchemaType();
    
    /**
     * @param type schema type
     * @return model representation
     */
    XmlValidateModel setSchemaType(XmlSchemaType type);
}
