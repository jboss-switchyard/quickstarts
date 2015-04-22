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
package org.switchyard.test.jca;

import javax.resource.cci.Record;

import org.switchyard.Exchange;
import org.switchyard.SwitchYardException;
import org.switchyard.component.common.SynchronousInOutHandler;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.common.selector.BaseOperationSelector;
import org.switchyard.component.jca.composer.StreamableRecordBindingData;
import org.switchyard.component.jca.endpoint.CCIEndpoint;
import org.switchyard.component.jca.processor.cci.StreamableRecord;
import org.switchyard.selector.OperationSelector;
import org.w3c.dom.Document;
/**
 * Concrete message endpoint class for JCA message inflow using JCA CCI MessageListener interface.
 */
public class MyStreamableRecordCCIEndpoint extends CCIEndpoint {
    
    private static final long DEFAULT_TIMEOUT = 15000;
    private static final String DEFAULT_RECORD_NAME = "StreamableRecord";
    private static final String DEFAULT_DESCRIPTION = "StreamableRecord implementation by " + MyStreamableRecordCCIEndpoint.class.getName();

    private long _waitTimeout = DEFAULT_TIMEOUT;
    private String _recordName = DEFAULT_RECORD_NAME;
    private String _description = DEFAULT_DESCRIPTION;
    private MessageComposer<StreamableRecordBindingData> _composer;
    private OperationSelector<StreamableRecordBindingData> _selector;
    
    @Override
    public void initialize() {
        setServiceReference(getServiceDomain().getServiceReference(getServiceQName()));
        _composer = getMessageComposer(StreamableRecordBindingData.class);
        _selector = new BaseOperationSelector<StreamableRecordBindingData>(getJCABindingModel().getOperationSelector()) {
            @Override
            protected Document extractDomDocument(StreamableRecordBindingData content) throws Exception {
                return null;
            }
            @Override
            protected String extractString(StreamableRecordBindingData content) throws Exception {
                return null;
            }
        };
    }
    
    @Override
    public Record onMessage(Record record) {
        SynchronousInOutHandler inOutHandler = new SynchronousInOutHandler();
        StreamableRecord sourceRecord = StreamableRecord.class.cast(record);
        try {
            StreamableRecordBindingData bindingData = new StreamableRecordBindingData(sourceRecord);
            String operation = _selector != null ? _selector.selectOperation(bindingData).getLocalPart() : null;
            Exchange exchange = createExchange(operation, inOutHandler);
            exchange.send(_composer.compose(bindingData, exchange));

            exchange = inOutHandler.waitForOut(_waitTimeout);
            StreamableRecord returnRecord = new StreamableRecord();
            returnRecord.setRecordName(_recordName);
            returnRecord.setRecordShortDescription(_description);
            returnRecord.setRecordShortDescription(_description);
            return _composer.decompose(exchange, new StreamableRecordBindingData(returnRecord)).getRecord();
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }
}
