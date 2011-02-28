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

package org.switchyard.transform.config.model;

import org.switchyard.config.model.transform.TransformModel;

/**
 * A "transform.smooks" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface SmooksTransformModel extends TransformModel {

    /** The "smooks" name. */
    public static final String SMOOKS = "smooks";
<<<<<<< HEAD

    /** The "config" name. */
=======
    public static final String TYPE = "type";
>>>>>>> changing smooks transformer config
    public static final String CONFIG = "config";

    /** The "reportPath" name. */
    public static final String REPORT_PATH = "reportPath";

<<<<<<< HEAD
    /**
     * Gets the config attribute.
     * @return the config attribute
     */
=======
    public String getTransformType();

    public SmooksTransformModel setTransformType(String type);

>>>>>>> changing smooks transformer config
    public String getConfig();

    /**
     * Sets the config attribute.
     * @param config the config attribute
     * @return this SmooksTransformModel (useful for chaining)
     */
    public SmooksTransformModel setConfig(String config);

    /**
     * Gets the reportPath attribute.
     * @return the reportPath attribute
     */
    public String getReportPath();

    /**
     * Sets the reportPath attribute.
     * @param reportPath the reportPath attribute
     * @return this SmooksTransformModel (useful for chaining)
     */
    public SmooksTransformModel setReportPath(String reportPath);

}
