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
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.jca.composer.MappedRecordBindingData;

/**
 * MappedRecord handler.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MappedRecordHandler extends RecordHandler<MappedRecordBindingData> {

    @Override
    public Message handle(Exchange exchange, Connection conn, Interaction interact) throws Exception {
        MappedRecord record = getRecordFactory().createMappedRecord(MappedRecordHandler.class.getName());
        MappedRecord outRecord = (MappedRecord) interact.execute(getInteractionSpec(), getMessageComposer(MappedRecordBindingData.class).decompose(exchange, new MappedRecordBindingData(record)).getRecord());
        return getMessageComposer(MappedRecordBindingData.class).compose(new MappedRecordBindingData(outRecord), exchange);
    }
}
