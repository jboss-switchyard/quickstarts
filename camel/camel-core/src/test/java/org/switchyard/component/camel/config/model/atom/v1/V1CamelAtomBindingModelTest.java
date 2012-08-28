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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.component.atom.AtomEndpoint;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.camel.config.model.CamelScheduledPollConsumer;
import org.switchyard.component.camel.config.model.atom.CamelAtomBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.component.camel.config.model.v1.V1CamelScheduledPollConsumer;
import org.switchyard.config.model.Validation;

/**
 * Test of atom binding model.
 */
public class V1CamelAtomBindingModelTest extends V1BaseCamelModelTest<V1CamelAtomBindingModel> {

    private static final String ATOM_XML = "switchyard-atom-binding.xml";

    private static final String ATOM_URI = 
        "atom://file:///dev/null?feedHeader=true&filter=true&lastUpdate=2011-01-01T12:00:00"
        + "&sortEntries=true&splitEntries=true&throttleEntries=true"
        + "&delay=15000&initialDelay=20000&useFixedDelay=true";

    private Date referenceDate;
    private static final URI FEED_URI = URI.create("file:///dev/null");
    private static final Integer INITIAL_DELAY = new Integer(20000);
    private static final Integer DELAY = new Integer(15000);
    private static final Boolean USE_FIXED_DELAY = true;

    @Before
    public void setUp() throws Exception {
        referenceDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .parse("2011-01-01T12:00:00");
    }

    @Test
    public void testAllConfigSettings() {
        CamelAtomBindingModel atomModel = createAtomModel();
        // Verify that what we put in is what we get out
        assertDefaults(atomModel);
    }

    @Test
    public void testReadConfig() throws Exception {
        CamelAtomBindingModel atomModel = getFirstCamelModelBinding(ATOM_XML);
        final Validation validateModel = atomModel.validateModel();

        assertTrue(validateModel.isValid());
        assertEquals(atomModel.getFeedURI(), FEED_URI);
        assertEquals(atomModel.getConsumer().getDelay(), DELAY);
        assertEquals(atomModel.isFiltered(), Boolean.TRUE);
    }

    @Test
    public void testWriteConfig() throws Exception {
        String refXml = getFirstCamelModelBinding(ATOM_XML).toString();
        String newXml = createAtomModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    @Test
    public void testComponentURI() {
        CamelAtomBindingModel atomModel = createAtomModel();
        assertEquals(ATOM_URI, atomModel.getComponentURI().toString());
    }

    @Test
    public void testCamelEndpoint() {
        CamelAtomBindingModel model = createAtomModel();
        AtomEndpoint endpoint = getEndpoint(model, AtomEndpoint.class);

        assertEquals(FEED_URI.toString(), endpoint.getFeedUri().toString());
        assertEquals(referenceDate, endpoint.getLastUpdate());
        assertTrue(endpoint.isFeedHeader());
        assertTrue(endpoint.isFilter());
        assertTrue(endpoint.isSortEntries());
        assertTrue(endpoint.isSplitEntries());
        assertTrue(endpoint.isThrottleEntries());
    }

    private void assertDefaults(CamelAtomBindingModel atomModel) {
        assertEquals(FEED_URI, atomModel.getFeedURI());
        assertEquals(DELAY, atomModel.getConsumer().getDelay());
        assertEquals(INITIAL_DELAY, atomModel.getConsumer().getInitialDelay());
        assertEquals(referenceDate.toString(), atomModel.getLastUpdate().toString());
        assertEquals(Boolean.TRUE, atomModel.isFeedHeader());
        assertEquals(Boolean.TRUE, atomModel.isFiltered());
        assertEquals(Boolean.TRUE, atomModel.getConsumer().isUseFixedDelay());
        assertEquals(Boolean.TRUE, atomModel.isSorted());
        assertEquals(Boolean.TRUE, atomModel.isSplit());
        assertEquals(Boolean.TRUE, atomModel.isThrottled());
    }

    private V1CamelAtomBindingModel createAtomModel() {
        V1CamelAtomBindingModel abm = new V1CamelAtomBindingModel()
            .setFeedHeader(true)
            .setFeedURI(FEED_URI)
            .setFiltered(true)
            .setLastUpdate(referenceDate)
            .setSorted(true)
            .setSplit(true)
            .setThrottled(true);

        CamelScheduledPollConsumer consumer = new V1CamelScheduledPollConsumer(V1CamelAtomBindingModel.CONSUME)
            .setDelay(15000)
            .setInitialDelay(20000)
            .setUseFixedDelay(true);
        abm.setConsumer(consumer);
        return abm;
    }
}
