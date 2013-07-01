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
package org.switchyard.component.jca.processor.cci;

import javax.resource.cci.Connection;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.Interaction;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.composer.IndexedRecordBindingData;

/**
 * IndexedRecordHandler.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class IndexedRecordHandler extends RecordHandler<IndexedRecordBindingData> {

    private MessageComposer<IndexedRecordBindingData> _composer;
    
    /**
     * Constructor which creates the message composer. 
     */
    public IndexedRecordHandler() {
        _composer = getMessageComposer(IndexedRecordBindingData.class);
    }
    
    @Override
    public Message handle(Exchange exchange, Connection conn, Interaction interact) throws Exception {
        IndexedRecord record = getRecordFactory().createIndexedRecord(IndexedRecordHandler.class.getName());
        IndexedRecord outRecord = (IndexedRecord) interact.execute(getInteractionSpec(), getMessageComposer(IndexedRecordBindingData.class).decompose(exchange, new IndexedRecordBindingData(record)).getRecord());
        return _composer.compose(new IndexedRecordBindingData(outRecord), exchange);
    }
}
