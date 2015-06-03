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
