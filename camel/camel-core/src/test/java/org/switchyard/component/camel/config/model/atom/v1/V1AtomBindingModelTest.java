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


package org.switchyard.component.camel.config.model.atom.v1;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.apache.camel.CamelContext;
import org.apache.camel.component.atom.AtomEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.component.camel.config.model.atom.AtomBindingModel;
import org.switchyard.config.model.ModelPuller;

public class V1AtomBindingModelTest {

    private static final String ATOM_XML = 
        "/org/switchyard/component/camel/config/model/v1/switchyard-atom-binding.xml";
    
    private static final String ATOM_URI = 
        "atom://file:///dev/null?consumer.delay=15000&feedHeader=true&filter=true"
        + "&consumer.userFixedDelay=true&consumer.initialDelay=20000&lastUpdate=2011-01-01T12:00:00"
        + "&sortEntries=true&splitEntries=true&throttleEntries=true";
    
    private Date referenceDate;
    private static final URI FEED_URI = URI.create("file:///dev/null");
    private static final Integer INITIAL_DELAY = new Integer(20000);
    private static final Integer DELAY = new Integer(15000);
    
    @Before
    public void setUp() throws Exception {
        referenceDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .parse("2011-01-01T12:00:00");
    }

    @Test
    public void testAllConfigSettings() {
        AtomBindingModel atomModel = createAtomModel();
        // Verify that what we put in is what we get out
        assertDefaults(atomModel);
    }
    
    @Test
    public void testConfigOverride() {
        // set a value on an existing config element
        AtomBindingModel atomModel = createAtomModel();
        Assert.assertEquals(DELAY, atomModel.getDelay());
        atomModel.setDelay(750);
        Assert.assertEquals(new Integer(750), atomModel.getDelay());
    }
    
    @Test
    public void testReadConfig() throws Exception {
        AtomBindingModel atomModel = new ModelPuller<AtomBindingModel>().pull(ATOM_XML, getClass());
        Assert.assertEquals(atomModel.getFeedURI(), FEED_URI);
        Assert.assertEquals(atomModel.getDelay(), DELAY);
        Assert.assertEquals(atomModel.isFiltered(), Boolean.TRUE);
    }
    
    @Test
    public void testWriteConfig() throws Exception {
        String refXml = new StringPuller().pull(ATOM_XML, getClass());
        String newXml = createAtomModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        Assert.assertTrue(diff.toString(), diff.similar());
    }
    
    @Test
    public void testComponentURI() {
        AtomBindingModel atomModel = createAtomModel();
        Assert.assertEquals(ATOM_URI, atomModel.getComponentURI().toString());
    }
    
    @Test
    public void testCamelEndpoint() {
        
        AtomBindingModel model = createAtomModel();
        String configUri = model.getComponentURI().toString();
        CamelContext context = new DefaultCamelContext();
        AtomEndpoint endpoint = context.getEndpoint(configUri, AtomEndpoint.class);
        
        Assert.assertEquals(FEED_URI.toString(), endpoint.getFeedUri().toString());
        Assert.assertEquals(referenceDate, endpoint.getLastUpdate());
        Assert.assertTrue(endpoint.isFeedHeader());
        Assert.assertTrue(endpoint.isFilter());
        Assert.assertTrue(endpoint.isSortEntries());
        Assert.assertTrue(endpoint.isSplitEntries());
        Assert.assertTrue(endpoint.isThrottleEntries());
    }
    
    
    private void assertDefaults(AtomBindingModel atomModel) {
        Assert.assertEquals(FEED_URI, atomModel.getFeedURI());
        Assert.assertEquals(DELAY, atomModel.getDelay());
        Assert.assertEquals(INITIAL_DELAY, atomModel.getInitialDelay());
        Assert.assertEquals(referenceDate.toString(), atomModel.getLastUpdate().toString());
        Assert.assertEquals(Boolean.TRUE, atomModel.isFeedHeader());
        Assert.assertEquals(Boolean.TRUE, atomModel.isFiltered());
        Assert.assertEquals(Boolean.TRUE, atomModel.isFixedDelay());
        Assert.assertEquals(Boolean.TRUE, atomModel.isSorted());
        Assert.assertEquals(Boolean.TRUE, atomModel.isSplit());
        Assert.assertEquals(Boolean.TRUE, atomModel.isThrottled());
    }
    
    private AtomBindingModel createAtomModel() {
        return new V1AtomBindingModel().setDelay(5)
            .setFeedHeader(true)
            .setFeedURI(FEED_URI)
            .setFixedDelay(true)
            .setFiltered(true)
            .setInitialDelay(INITIAL_DELAY)
            .setDelay(DELAY)
            .setLastUpdate(referenceDate)
            .setSorted(true)
            .setSplit(true)
            .setThrottled(true);
    }
}
