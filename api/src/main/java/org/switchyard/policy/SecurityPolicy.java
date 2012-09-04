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
package org.switchyard.policy;

/**
 * Supported types for <a href="http://docs.oasis-open.org/opencsa/sca-policy/sca-policy-1.1-spec-csprd03.html#_Toc311121482">Security Policy</a>.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public enum SecurityPolicy implements Policy {

    /**
     * Ensure that the client is authenticated by the server.
     */
    CLIENT_AUTHENTICATION("clientAuthentication") {
        @Override
        public PolicyType getType() {
            return PolicyType.INTERACTION;
        }
    },
    
    /**
     * Ensure that only authorized entities can view the contents of a message.
     */
    CONFIDENTIALITY("confidentiality") {
        @Override
        public PolicyType getType() {
            return PolicyType.INTERACTION;
        }
    };

    private String _name;

    private SecurityPolicy(String name) {
        _name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCompatibleWith(Policy target) {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Policy getPolicyDependency() {
        return null;
    }
}
