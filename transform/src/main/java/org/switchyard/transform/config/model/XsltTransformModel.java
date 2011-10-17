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
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.switchyard.transform.config.model;

import org.switchyard.config.model.transform.TransformModel;

/**
 * @author <a href="mailto:aamonten@gmail.com">Alejandro Montenegro</a>
 */
public interface XsltTransformModel extends TransformModel {

    /** The "xslt" name. */
    String XSLT = "xslt";

    /** The "xslt file" location. */
    String XSLT_FILE_URI = "xsltFile";

    /**  whether a warning should be reported as an Exception.   */
    String FAIL_ON_WARNING = "failOnWarning";
    
    /**
     * @return xslt identifier
     */
    String getXsltFile();

    /** Set xslt identifier.
     * @param xsltFile URI of xslt file
     * @return model representation
     */
    XsltTransformModel setXsltFile(String xsltFile);

    /** Return whether a warning should be reported as an SwitchYardException.
     * If failOnWarning attribute is "true", then a warning should be reported
     * as an SwitchYardException, otherwise just log.
     * @return whether a warning should be reported as an SwitchYardException
     */
    boolean failOnWarning();
    
    /** Set whether a warning should be reported as an SwitchYardException.
     * If failOnWarning attribute is "true", then a warning should be reported
     * as an SwitchYardException, otherwise just log.
     * @param failOnWarning whether a warning should be reported as an SwitchYardException
     * @return model representation
     */
    XsltTransformModel setFailOnWarning(boolean failOnWarning);
}
