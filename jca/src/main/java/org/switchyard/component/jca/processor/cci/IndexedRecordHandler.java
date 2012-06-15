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
package org.switchyard.component.jca.processor.cci;

import javax.resource.cci.Connection;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.RecordFactory;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.jca.composer.JCAComposition;

/**
 * IndexedRecordHandler.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class IndexedRecordHandler extends RecordHandler<IndexedRecord> {

    /**
     * Constructor.
     */
    public IndexedRecordHandler() {
        setMessageComposer(JCAComposition.getMessageComposer(IndexedRecord.class));
    }

    @Override
    public Message handle(Exchange exchange, RecordFactory factory, InteractionSpec interactionSpec, Connection conn, Interaction interact) throws Exception {
        IndexedRecord record = factory.createIndexedRecord(IndexedRecordHandler.class.getName());
        IndexedRecord outRecord = (IndexedRecord) interact.execute(interactionSpec, getMessageComposer().decompose(exchange, record));
        return getMessageComposer().compose(outRecord, exchange, true);
    }
}
