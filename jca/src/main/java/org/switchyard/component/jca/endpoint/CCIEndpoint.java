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
package org.switchyard.component.jca.endpoint;

import javax.naming.InitialContext;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.MessageListener;
import javax.resource.cci.Record;
import javax.resource.cci.RecordFactory;

import org.switchyard.Exchange;
import org.switchyard.component.common.SynchronousInOutHandler;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.composer.MappedRecordBindingData;
import org.switchyard.SwitchYardException;
import org.switchyard.selector.OperationSelector;
/**
 * Concrete message endpoint class for JCA message inflow using JCA CCI MessageListener interface.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class CCIEndpoint extends AbstractInflowEndpoint implements MessageListener {
    
    private static final long DEFAULT_TIMEOUT = 15000;
    private static final String DEFAULT_RECORD_NAME = "DefaultMappedRecord";
    private static final String DEFAULT_DESCRIPTION = "Default MappedRecord implementation by " + CCIEndpoint.class.getName();

    private String _connectionFactoryJNDIName;
    private long _waitTimeout = DEFAULT_TIMEOUT;
    private String _recordName = DEFAULT_RECORD_NAME;
    private String _description = DEFAULT_DESCRIPTION;
    private MessageComposer<MappedRecordBindingData> _composer;
    private OperationSelector<MappedRecordBindingData> _selector;
    private RecordFactory _recordFactory;
    
    @Override
    public void initialize() {
        super.initialize();
        
        _composer = getMessageComposer(MappedRecordBindingData.class);
        _selector = getOperationSelector(MappedRecordBindingData.class);

        try {
            ConnectionFactory factory = (ConnectionFactory) new InitialContext().lookup(_connectionFactoryJNDIName);
            _recordFactory = factory.getRecordFactory();
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
    @Override
    public Record onMessage(Record record) {
        SynchronousInOutHandler inOutHandler = new SynchronousInOutHandler();
        MappedRecord sourceRecord = MappedRecord.class.cast(record);
        try {
            MappedRecordBindingData bindingData = new MappedRecordBindingData(sourceRecord);
            String operation = _selector != null ? _selector.selectOperation(bindingData).getLocalPart() : null;
            Exchange exchange = createExchange(operation, inOutHandler);
            exchange.send(_composer.compose(bindingData, exchange));

            exchange = inOutHandler.waitForOut(_waitTimeout);
            MappedRecord returnRecord = _recordFactory.createMappedRecord(_recordName);
            returnRecord.setRecordShortDescription(_description);
            return _composer.decompose(exchange, new MappedRecordBindingData(returnRecord)).getRecord();
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
    /**
     * set implementation class name for {@link RecordFactory}.
     * 
     * @param name class name
     */
    public void setConnectionFactoryJNDIName(String name) {
        _connectionFactoryJNDIName = name;
    }
    
    /**
     * get implementation class name for {@link RecordFactory}.
     * 
     * @return class name
     */
    public String getConnectionFactoryJNDIName() {
        return _connectionFactoryJNDIName;
    }
}
