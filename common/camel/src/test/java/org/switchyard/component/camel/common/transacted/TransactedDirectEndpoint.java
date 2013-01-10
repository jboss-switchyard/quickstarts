/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel.common.transacted;

import java.util.Map;

import org.apache.camel.component.direct.DirectConsumer;
import org.apache.camel.component.direct.DirectEndpoint;
import org.springframework.transaction.PlatformTransactionManager;

public class TransactedDirectEndpoint extends DirectEndpoint {

    private PlatformTransactionManager manager;

    public TransactedDirectEndpoint(String uri, TransactedDirectComponent component, Map<String, DirectConsumer> consumers) {
        super(uri, component, consumers);
    }

    public void setTransactionManager(PlatformTransactionManager manager) {
        this.manager = manager;
    }

    public PlatformTransactionManager getTransactionManager() {
        return manager;
    }

}
