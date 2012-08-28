/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.config.model.quartz.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.component.camel.config.model.quartz.CamelQuartzBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelQuartzBindingModel}.
 *
 * @author Lukasz Dywicki
 */
public class V1CamelQuartzBindingModelTest extends V1BaseCamelModelTest<V1CamelQuartzBindingModel> {

    private static final String CAMEL_XML = "switchyard-quartz-binding-beans.xml";


    private static final String COMPONENT_URI = UnsafeUriCharactersEncoder.encode(
        "quartz://MyJob?cron=0 0 12 * * ?&stateful=true&trigger.startTime=2011-01-01T12:00:00&trigger.endTime=2011-01-01T12:00:00"
    );

    // Used for dateTime fields
    private static DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final String NAME = "MyJob";
    private static final String CRON = "0 0 12 * * ?";
    private static final Boolean STATEFUL = true;
    private static Date START_TIME;
    private static Date END_TIME;

    static {
        try {
            START_TIME = _dateFormat.parse("2011-01-01T12:00:00");
            END_TIME = _dateFormat.parse("2011-01-01T12:00:00");
        } catch (Exception e) { /* ignore */ }
    }

    @Test
    public void validateCamelBinding() throws Exception {
        final V1CamelQuartzBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        assertTrue(validateModel.isValid());
        assertModel(bindingModel);
        assertEquals(COMPONENT_URI, bindingModel.getComponentURI().toString());
    }

    @Test
    public void verifyProtocol() {
        V1CamelQuartzBindingModel model = new V1CamelQuartzBindingModel();
        model.setName(NAME);
        model.setCron(CRON);

        String uri = model.getComponentURI().toString();
        assertTrue(uri.startsWith(V1CamelQuartzBindingModel.QUARTZ + "://" + NAME));
        assertTrue(model.validateModel().isValid());
    }


    @Test
    public void compareWriteConfig() throws Exception {
        String refXml = getFirstCamelBinding(CAMEL_XML).toString();
        String newXml = createModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        assertTrue(diff.toString(), diff.similar());
    }

    private V1CamelQuartzBindingModel createModel() {
        return (V1CamelQuartzBindingModel) new V1CamelQuartzBindingModel()
            .setName(NAME)
            .setCron(CRON)
            .setStateful(STATEFUL)
            .setStartTime(START_TIME)
            .setEndTime(END_TIME);
    }

    private void assertModel(CamelQuartzBindingModel model) {
        assertEquals(NAME, model.getName());
        assertEquals(CRON, model.getCron());
        assertEquals(STATEFUL, model.isStateful());
        assertEquals(START_TIME, model.getStartTime());
        assertEquals(END_TIME, model.getEndTime());
    }

}