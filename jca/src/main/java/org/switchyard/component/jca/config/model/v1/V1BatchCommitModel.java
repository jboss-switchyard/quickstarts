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
package org.switchyard.component.jca.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.jca.config.model.BatchCommitModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * V1 BatchCommitModel.
 */
public class V1BatchCommitModel extends BaseModel implements BatchCommitModel {

    /**
     * Constructor.
     */
    public V1BatchCommitModel() {
        super(new QName(JCAConstants.DEFAULT_NAMESPACE, JCAConstants.BATCH_COMMIT));
    }
    
    /**
     * Constructor.
     * @param config configuration
     * @param desc description
     */
    public V1BatchCommitModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }
    
    @Override
    public long getBatchTimeout() {
        return Long.parseLong(getModelAttribute(JCAConstants.BATCH_TIMEOUT));
    }

    @Override
    public BatchCommitModel setBatchTimeout(long delay) {
        setModelAttribute(JCAConstants.BATCH_TIMEOUT, Long.toString(delay));
        return this;
    }

    @Override
    public int getBatchSize() {
        return Integer.parseInt(getModelAttribute(JCAConstants.BATCH_SIZE));
    }

    @Override
    public BatchCommitModel setBatchSize(int size) {
        setModelAttribute(JCAConstants.BATCH_SIZE, Integer.toString(size));
        return this;
    }

}
