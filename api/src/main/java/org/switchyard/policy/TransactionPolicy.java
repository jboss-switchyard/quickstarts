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
package org.switchyard.policy;

/**
 * Supported types for <a href="http://docs.oasis-open.org/opencsa/sca-policy/sca-policy-1.1-spec-csprd03.html#_Toc311121490">Transaction Policy</a>.
 * 
 * @author Keith Babo &copy; 2011 Red Hat Inc.
 */
public enum TransactionPolicy implements Policy {

    /** Transaction implementation policies */
    /**
     * Uses any existing global transaction propagated from the client
     * or else begins and complete a new global transaction.
     */
    MANAGED_TRANSACTION_GLOBAL("managedTransaction.Global") {
        @Override
        public boolean supports(PolicyType type) {
            return type == PolicyType.IMPLEMENTATION;
        }
        @Override
        public boolean isCompatibleWith(Policy target) {
            if (target == MANAGED_TRANSACTION_LOCAL
                    || target == NO_MANAGED_TRANSACTION) {
                return false;
            }
            return true;
        }
    },
    
    /**
     * SwitchYard runtime begins and complete a Local Transaction Containment for each Exchange.
     */
    MANAGED_TRANSACTION_LOCAL("managedTransaction.Local") {
        @Override
        public boolean supports(PolicyType type) {
            return type == PolicyType.IMPLEMENTATION;
        }
        @Override
        public boolean isCompatibleWith(Policy target) {
            if (target == MANAGED_TRANSACTION_GLOBAL
                    || target == NO_MANAGED_TRANSACTION
                    || target == PROPAGATES_TRANSACTION) {
                return false;
            }
            return true;
        }
        @Override
        public Policy getPolicyDependency() {
            return SUSPENDS_TRANSACTION;
        }
    },
    
    /**
     * SwitchYard runtime doesn't start any transaction. Application has a responsibility
     * to manage the transaction boundaries.
     */
    NO_MANAGED_TRANSACTION("noManagedTransaction") {
        @Override
        public boolean supports(PolicyType type) {
            return type == PolicyType.IMPLEMENTATION;
        }
        @Override
        public boolean isCompatibleWith(Policy target) {
            if (target == MANAGED_TRANSACTION_GLOBAL
                    || target == MANAGED_TRANSACTION_LOCAL
                    || target == PROPAGATES_TRANSACTION) {
                return false;
            }
            return true;
        }
    },

    /** Transaction interaction policies */
    /**
     * Any existing transaction should continue and be used during the invocation.
     */
    PROPAGATES_TRANSACTION("propagatesTransaction") {
        @Override
        public boolean supports(PolicyType type) {
            return type == PolicyType.INTERACTION;
        }
        @Override
        public boolean isCompatibleWith(Policy target) {
            if (target == MANAGED_TRANSACTION_LOCAL
                    || target == NO_MANAGED_TRANSACTION
                    || target == SUSPENDS_TRANSACTION) {
                return false;
            }
            return true;
        }
    },
    
    /**
     * Any existing transaction should be suspended before the invocation takes place.
     */
    SUSPENDS_TRANSACTION("suspendsTransaction") {
        @Override
        public boolean supports(PolicyType type) {
            return type == PolicyType.INTERACTION;
        }
        @Override
        public boolean isCompatibleWith(Policy target) {
            if (target == PROPAGATES_TRANSACTION) {
                return false;
            }
            return true;
        }
    };

    private String _name;

    private TransactionPolicy(String name) {
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

    @Override
    public Policy getPolicyDependency() {
        return null;
    }
}
