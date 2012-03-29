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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.resource.cci.MappedRecord;
import javax.resource.cci.MessageListener;
import javax.resource.cci.Record;

import org.switchyard.Exchange;
import org.switchyard.SynchronousInOutHandler;
import org.switchyard.component.common.composer.MessageComposer;
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

    private long _waitTimeout = DEFAULT_TIMEOUT;
    private String _recordName = DEFAULT_RECORD_NAME;
    private String _description = DEFAULT_DESCRIPTION;
    private MessageComposer<MappedRecord> _composer;
    
    @Override
    public Record onMessage(Record record) {
    
        if (_composer == null) {
            _composer = getMessageComposer(MappedRecord.class);
        }
        
        SynchronousInOutHandler inOutHandler = new SynchronousInOutHandler();
        Exchange exchange = createExchange(inOutHandler);
        try {
            exchange.send(_composer.compose((MappedRecord)record, exchange, true));

            exchange = inOutHandler.waitForOut(_waitTimeout);
            MappedRecord returnRecord = new DefaultMappedRecord();
            returnRecord.setRecordName(_recordName);
            returnRecord.setRecordShortDescription(_description);
            return _composer.decompose(exchange, returnRecord);
        } catch (Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    private class DefaultMappedRecord implements MappedRecord {

        private static final long serialVersionUID = 6036209388088632116L;
        private String _recordName;
        private String _description;
        private HashMap<Object,Object> _map = new HashMap<Object,Object>();
        
        @Override
        public String getRecordName() {
            return _recordName;
        }

        @Override
        public void setRecordName(String name) {
            _recordName = name;
        }

        @Override
        public void setRecordShortDescription(String description) {
            _description = description;
        }

        @Override
        public String getRecordShortDescription() {
            return _description;
        }

        @Override
        public int size() {
            return _map.size();
        }

        @Override
        public boolean isEmpty() {
            return _map.size() == 0;
        }

        @Override
        public boolean containsKey(Object key) {
            return _map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return _map.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return _map.get(key);
        }

        @Override
        public Object put(Object key, Object value) {
            return _map.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            return _map.remove(key);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void putAll(Map m) {
            _map.putAll(m);
        }

        @Override
        public void clear() {
            _map.clear();
        }

        @Override
        public Set keySet() {
            return _map.keySet();
        }

        @Override
        public Collection values() {
            return _map.values();
        }

        @Override
        public Set entrySet() {
            return _map.entrySet();
        }

        @Override
        public Object clone() {
            DefaultMappedRecord cloned = new DefaultMappedRecord();
            cloned.setRecordName(getRecordName().toString());
            cloned.setRecordShortDescription(getRecordShortDescription().toString());
            cloned.putAll((HashMap)_map.clone());
            return cloned;
        }
    }
}
