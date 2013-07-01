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
import javax.resource.cci.Interaction;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.composer.StreamableRecordBindingData;

/**
 * StreamableRecordHandler.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 * @author Antti Laisi
 */
public class StreamableRecordHandler extends RecordHandler<StreamableRecordBindingData> {

    private MessageComposer<StreamableRecordBindingData> _composer;
    
    /**
     * Constructor which creates the message composer. 
     */
    public StreamableRecordHandler() {
        _composer = getMessageComposer(StreamableRecordBindingData.class);
    }
    
    @Override
    public Message handle(Exchange exchange, Connection conn, Interaction interact) throws Exception {
        StreamableRecord record = new StreamableRecord();
        StreamableRecord outRecord = new StreamableRecord();
        interact.execute(getInteractionSpec(), getMessageComposer(StreamableRecordBindingData.class).decompose(exchange, new StreamableRecordBindingData(record)).getRecord(), outRecord);
        return _composer.compose(new StreamableRecordBindingData(outRecord), exchange);
    }

}
