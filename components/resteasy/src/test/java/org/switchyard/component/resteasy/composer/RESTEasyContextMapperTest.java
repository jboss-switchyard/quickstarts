package org.switchyard.component.resteasy.composer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.Context;
import org.switchyard.internal.DefaultContext;

/**
 * 
 * Test for SWITCHYARD-1990 to determine whether we are succesfully mapping non-Strings
 * from the Context to Strings in the binding data. 
 * 
 * @author tcunning
 */
public class RESTEasyContextMapperTest {

    @Before
    public void setUp() throws Exception {
    }
    
    /*
     * Test for SWITCHYARD-1990 to determine whether we are succesfully mapping non-Strings
     * from the Context to Strings in the binding data.
     */
    @Test
    public void mapToTest() throws Exception {
        RESTEasyContextMapper rcm = new RESTEasyContextMapper();
        RESTEasyBindingData rbd = new RESTEasyBindingData();

        Context context = new DefaultContext();
        context.setProperty("one", Integer.valueOf(1));
        
        rcm.mapTo(context, rbd);
        Iterator<Map.Entry<String, List<String>>> entries = rbd.getHeaders().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<String>> entry = entries.next();
            List<String> values = entry.getValue();
            Assert.assertTrue(values.size() == 1);
            Assert.assertTrue(entry.getKey().equals("one"));
            Assert.assertTrue(values.get(0).equals("1"));
        }
        
        RESTEasyBindingData rbd2 = new RESTEasyBindingData();
        context.removeProperties();
        List<Integer> list = new ArrayList<Integer>();
        list.add(Integer.valueOf(1));
        list.add(Integer.valueOf(2));
        list.add(Integer.valueOf(3));
        context.setProperty("numbers", list);
        rcm.mapTo(context, rbd2);
        entries = rbd2.getHeaders().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<String>> entry = entries.next();
            List<String> values = entry.getValue();
            Assert.assertTrue(values.size() == 3);
            Assert.assertTrue(entry.getKey().equals("numbers"));
            for (String value : values) {
                Assert.assertTrue(values.get(0).equals("1") || values.get(1).equals("2") ||
                        values.get(1).equals("3"));
            }
        }        
    }
        
    @After
    public void tearDown() throws Exception {
    }
}
