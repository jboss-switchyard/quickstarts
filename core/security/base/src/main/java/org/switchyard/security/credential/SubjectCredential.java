/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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

import javax.security.auth.Subject;

/**
 * SubjectCredential.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class SubjectCredential implements Credential {

    private static final long serialVersionUID = -2683242579779692195L;
    private static final String FORMAT = SubjectCredential.class.getSimpleName() + "@%s[subject=%s]";

    private final Subject _subject;

    /**
     * Constructs a SubjectCredentail with the specified subject.
     * @param subject the specified Subject
     */
    public SubjectCredential(Subject subject) {
        _subject = subject;
    }

    /**
     * Gets the Subject.
     * @return the Subject
     */
    public Subject getSubject() {
        return _subject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(FORMAT, System.identityHashCode(this), _subject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_subject == null) ? 0 : _subject.hashCode());
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
        SubjectCredential other = (SubjectCredential)obj;
        if (_subject == null) {
            if (other._subject != null) {
                return false;
            }
        } else if (!_subject.equals(other._subject)) {
            return false;
        }
        return true;
    }

}
