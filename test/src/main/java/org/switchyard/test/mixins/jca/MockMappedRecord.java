package org.switchyard.test.mixins.jca;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.resource.cci.MappedRecord;

/**
 * MockMappedRecord.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MockMappedRecord implements MappedRecord {

    private static final long serialVersionUID = 1L;
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
        MockMappedRecord cloned = new MockMappedRecord();
        cloned.setRecordName(getRecordName().toString());
        cloned.setRecordShortDescription(getRecordShortDescription().toString());
        cloned.putAll((HashMap)_map.clone());
        return cloned;
    }
}
