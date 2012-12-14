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
package org.switchyard.component.jca.config.model;

import org.switchyard.config.model.Model;

/**
 * binding.jca/inboundInteraction/batchCommit model.
 */
public interface BatchCommitModel extends Model {
    
    /**
     * get batch timeout.
     * @return batch timeout in milliseconds
     */
    long getBatchTimeout();
    
    /**
     * set batch timeout.
     * @param timeout batch timeout in milliseconds
     * @return {@link BatchCommitModel} to support method chaining
     */
    BatchCommitModel setBatchTimeout(long timeout);
    
    /**
     * get batch size.
     * @return batch size
     */
    int getBatchSize();
    
    /**
     * set batch size.
     * @param size batch size.
     * @return {@link BatchCommitModel} to support method chaining
     */
    BatchCommitModel setBatchSize(int size);
    
}
