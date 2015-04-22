package org.switchyard.component.http.composer;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.internal.DefaultContext;

public class HttpContextMapperTest {
    private DefaultContext _context;
    
    @Before
    public void setUp() throws Exception {
        _context = new DefaultContext(Scope.EXCHANGE);
    }
    
    @Test
    public void mapStrings() throws Exception {
        _context.setProperty("foo", "boo");
        HttpContextMapper hcm = new HttpContextMapper();
        HttpRequestBindingData bindingData = new HttpRequestBindingData();
        hcm.mapTo(_context, bindingData);
        Map<String, List<String>> headers = bindingData.getHeaders();
        Iterator it = headers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ArrayList list = (ArrayList) pairs.getValue();
            for (int i = 0; i < list.size(); i++) {
                assertTrue(list.get(i) instanceof String);                
            }
            it.remove();
        }
    }
    
    @Test
    public void mapObjects() throws Exception {
        ArrayList obj = new ArrayList();
        obj.add(new Integer(3));
        _context.setProperty("foo", obj);
        HttpContextMapper hcm = new HttpContextMapper();
        HttpRequestBindingData bindingData = new HttpRequestBindingData();
        hcm.mapTo(_context, bindingData);
        Map<String, List<String>> headers = bindingData.getHeaders();
        Iterator it = headers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ArrayList list = (ArrayList) pairs.getValue();
            for (int i = 0; i < list.size(); i++) {
                assertTrue(list.get(i) instanceof String);                
            }
            it.remove();
        }
    }
}
