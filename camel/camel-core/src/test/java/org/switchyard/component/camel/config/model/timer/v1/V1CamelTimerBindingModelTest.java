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

package org.switchyard.component.camel.config.model.timer.v1;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.camel.CamelContext;
import org.apache.camel.component.timer.TimerEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.camel.config.model.timer.CamelTimerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1CamelBindingModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.Validation;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;


/**
 * Test for {@link V1CamelBindingModel}.
 * 
 * @author Mario Antollini
 */
public class V1CamelTimerBindingModelTest {
    
	
	private static final String CAMEL_XML = "switchyard-timer-binding-beans.xml";
	
	private static final String NAME = "fooTimer";
	private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	private static final Long PERIOD = new Long(555);
	private static final Long DELAY = new Long(100);
	private static final Boolean FIXED_RATE = Boolean.TRUE;
	private static final Boolean DAEMON = Boolean.FALSE;

	private static final String CAMEL_URI = 
		"timer://fooTimer?time=2011-01-01T12:00:00&pattern=yyyy-MM-dd'T'HH:mm:ss&" +
		"period=555&delay=100&fixedRate=true&daemon=false";
	
	private static final String CAMEL_ENDPOINT_URI = "timer://fooTimer?" +
			"daemon=false&delay=100&fixedRate=true&pattern=yyyy-MM-dd%27T%27HH%3Amm%3Ass&" +
			"period=555&time=2011-01-01T12%3A00%3A00";
	
    private Date referenceDate;
	
	@Before
    public void setUp() throws Exception {
        referenceDate = new SimpleDateFormat(PATTERN).parse("2011-01-01T12:00:00");
    }

    @Test
    public void testConfigOverride() {
        // Set a value on an existing config element
        CamelTimerBindingModel bindingModel = createTimerModel();
        Assert.assertEquals(DELAY, bindingModel.getDelay());
        bindingModel.setDelay(new Long(999));
        Assert.assertEquals(new Integer(999).toString(), bindingModel.getDelay().toString());
    }
    
    @Test
    public void testReadConfig() throws Exception {
        final V1CamelTimerBindingModel bindingModel = getCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        Assert.assertEquals(validateModel.isValid(), true);
        //Camel File
        Assert.assertEquals(bindingModel.getName(), NAME);
        Assert.assertEquals(bindingModel.getTime().toString(), referenceDate.toString());
        Assert.assertEquals(bindingModel.getPattern(), PATTERN);
        Assert.assertEquals(bindingModel.getPeriod(), PERIOD);
        Assert.assertEquals(bindingModel.getDelay(), DELAY);
        Assert.assertEquals(bindingModel.isFixedRate(), FIXED_RATE);
        Assert.assertEquals(bindingModel.isDaemon(), DAEMON);
        Assert.assertEquals(bindingModel.getComponentURI().toString(), CAMEL_URI);
    }
    
    @Test
    public void testWriteConfig() throws Exception {
    	CamelTimerBindingModel bindingModel = createTimerModel();
        final Validation validateModel = bindingModel.validateModel();
        //Valid Model?
        Assert.assertEquals(validateModel.isValid(), true);
        //Camel File
        Assert.assertEquals(bindingModel.getName(), NAME);
        Assert.assertEquals(bindingModel.getTime().toString(), referenceDate.toString());
        Assert.assertEquals(bindingModel.getPattern(), PATTERN);
        Assert.assertEquals(bindingModel.getPeriod(), PERIOD);
        Assert.assertEquals(bindingModel.getDelay(), DELAY);
        Assert.assertEquals(bindingModel.isFixedRate(), FIXED_RATE);
        Assert.assertEquals(bindingModel.isDaemon(), DAEMON);
        Assert.assertEquals(bindingModel.getComponentURI().toString(), CAMEL_URI);
    }
    
    @Test
    public void compareWriteConfig() throws Exception {
    	String refXml = getCamelBinding(CAMEL_XML).toString();
        String newXml = createTimerModel().toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(refXml, newXml);
        Assert.assertTrue(diff.toString(), diff.similar());
    }
	
    @Test
    public void testCamelEndpoint() {
        CamelTimerBindingModel model = createTimerModel();
        String configUri = model.getComponentURI().toString();
        CamelContext context = new DefaultCamelContext();
        TimerEndpoint endpoint = context.getEndpoint(configUri, TimerEndpoint.class);
        Assert.assertEquals(endpoint.getTimerName(), NAME);
        Assert.assertEquals(endpoint.getTime().toString(), referenceDate.toString());
        Assert.assertEquals(endpoint.getPeriod(), PERIOD.longValue());
        Assert.assertEquals(endpoint.getDelay(), DELAY.longValue());
        Assert.assertEquals(endpoint.isFixedRate(), FIXED_RATE.booleanValue());
        Assert.assertEquals(endpoint.isDaemon(), DAEMON.booleanValue());
        Assert.assertEquals(endpoint.getEndpointUri().toString(), CAMEL_ENDPOINT_URI);
    }
    
    private CamelTimerBindingModel createTimerModel() {
    	return new V1CamelTimerBindingModel().setName(NAME)
    		.setTime(referenceDate)
    		.setPattern(PATTERN)
    		.setPeriod(PERIOD)
    		.setDelay(DELAY)
    		.setFixedRate(FIXED_RATE)
    		.setDaemon(DAEMON);
    }
    
    
    private V1CamelTimerBindingModel getCamelBinding(final String config) throws Exception {
        final InputStream in = getClass().getResourceAsStream(config);
        final SwitchYardModel model = (SwitchYardModel) new ModelPuller<SwitchYardModel>().pull(in);
        final List<CompositeServiceModel> services = model.getComposite().getServices();
        final CompositeServiceModel compositeServiceModel = services.get(0);
        final List<BindingModel> bindings = compositeServiceModel.getBindings();
        return (V1CamelTimerBindingModel) bindings.get(0);
    }
    
}
