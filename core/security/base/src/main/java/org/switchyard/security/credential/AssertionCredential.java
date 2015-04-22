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
package org.switchyard.security.credential;

import org.switchyard.common.xml.XMLHelper;
import org.w3c.dom.Element;

/**
 * AssertionCredential.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class AssertionCredential implements Credential {

    private static final long serialVersionUID = -6370905027144656543L;
    private static final String FORMAT = AssertionCredential.class.getSimpleName() + "@%s[assertion=%s]";

    private final Element _assertion;
    private final String _assertionXML;

    /**
     * Constructs a AssertionCredential with the specified assertion.
     * @param assertion the specified assertion
     */
    public AssertionCredential(Element assertion) {
        _assertion = assertion;
        _assertionXML = _assertion != null ? XMLHelper.toString(_assertion).trim() : null;
    }

    /**
     * Gets the assertion Element.
     * @return the assertion Element
     */
    public Element getAssertion() {
        return _assertion;
    }

    /**
     * Gets the assertion XML.
     * @return the assertion XML
     */
    public String getAssertionXML() {
        return _assertionXML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(FORMAT, System.identityHashCode(this), _assertionXML);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_assertionXML == null) ? 0 : _assertionXML.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AssertionCredential other = (AssertionCredential)obj;
        if (_assertionXML == null) {
            if (other._assertionXML != null) {
                return false;
            }
        } else if (!_assertionXML.equals(other._assertionXML)) {
            return false;
        }
        return true;
    }

}
