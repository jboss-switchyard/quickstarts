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
package org.switchyard.component.jca.endpoint;

import javax.naming.InitialContext;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.MessageListener;
import javax.resource.cci.Record;
import javax.resource.cci.RecordFactory;

import org.switchyard.Exchange;
import org.switchyard.SynchronousInOutHandler;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.composer.MappedRecordBindingData;
import org.switchyard.exception.SwitchYardException;
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
    private RecordFactory _recordFactory;
    
    @Override
    public void initialize() {
        super.initialize();
        
        _composer = getMessageComposer(MappedRecordBindingData.class);
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
        Exchange exchange = createExchange(inOutHandler);
        try {
            exchange.send(_composer.compose(new MappedRecordBindingData((MappedRecord)record), exchange, true));

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
