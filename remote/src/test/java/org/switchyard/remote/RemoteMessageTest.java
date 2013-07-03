/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.remote;

import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.HandlerException;
import org.switchyard.SwitchYardException;
import org.switchyard.common.io.pull.ElementPuller;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;
import org.w3c.dom.Element;

/**
 * Tests RemoteMessage de/serialization scenarios.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class RemoteMessageTest {

    private <T> T serDeser(T object, Class<T> clazz) throws Exception {
        Serializer ser = SerializerFactory.create(FormatType.JSON, null, true);
        byte[] bytes = ser.serialize(object, clazz);
        return ser.deserialize(bytes, clazz);
    }

    @Test
    public void testDOMProperty() throws Exception {
        final String expectedXML = "<one number=\"1\"><two number=\"2\"/></one>";
        final Element expectedDOM = new ElementPuller().pull(new StringReader(expectedXML));
        RemoteMessage msg = new RemoteMessage();
        msg.getContext().setProperty("one", expectedDOM);
        msg = serDeser(msg, RemoteMessage.class);
        final Element actualDOM = (Element)msg.getContext().getProperty("one").getValue();
        final String actualXML = XMLHelper.toString(actualDOM);
        Assert.assertEquals(expectedXML, actualXML);
    }

    @Test
    public void testDOMContent() throws Exception {
        final String expectedXML = "<one number=\"1\"><two number=\"2\"/></one>";
        final Element expectedDOM = new ElementPuller().pull(new StringReader(expectedXML));
        RemoteMessage msg = new RemoteMessage();
        msg.setContent(expectedDOM);
        msg = serDeser(msg, RemoteMessage.class);
        final Element actualDOM = (Element)msg.getContent();
        final String actualXML = XMLHelper.toString(actualDOM);
        Assert.assertEquals(expectedXML, actualXML);
    }

    @Test
    public void testThrowableContent() throws Exception {
        // create hierarchy
        final Exception e_pre = new Exception("e");
        e_pre.fillInStackTrace();
        final HandlerException he1_pre = new HandlerException(e_pre);
        he1_pre.fillInStackTrace();
        final RuntimeException re_pre = new RuntimeException("re", he1_pre);
        re_pre.fillInStackTrace();
        final SwitchYardException sye_pre = new SwitchYardException("sye", re_pre);
        sye_pre.fillInStackTrace();
        final HandlerException he2_pre = new HandlerException("he", sye_pre);
        he2_pre.fillInStackTrace();
        // create message
        RemoteMessage msg = new RemoteMessage();
        msg.setContent(he2_pre);
        // serialize and deserialize
        msg = serDeser(msg, RemoteMessage.class);
        // get causes
        final HandlerException he2_post = (HandlerException)msg.getContent();
        final SwitchYardException sye_post = (SwitchYardException)he2_post.getCause();
        final RuntimeException re_post = (RuntimeException)sye_post.getCause();
        final HandlerException he1_post = (HandlerException)re_post.getCause();
        final Exception e_post = (Exception)he1_post.getCause();
        // test wrapper
        Assert.assertEquals(he2_pre.isWrapper(), he2_post.isWrapper());
        Assert.assertEquals(he1_pre.isWrapper(), he1_post.isWrapper());
        // test messages
        Assert.assertEquals(he2_pre.getMessage(), he2_post.getMessage());
        Assert.assertEquals(sye_pre.getMessage(), sye_post.getMessage());
        Assert.assertEquals(re_pre.getMessage(), re_post.getMessage());
        Assert.assertEquals(he1_pre.getMessage(), he1_post.getMessage());
        Assert.assertEquals(e_pre.getMessage(), e_post.getMessage());
        // test stack traces
        Assert.assertEquals(he2_pre.getStackTrace().length, he2_post.getStackTrace().length);
        Assert.assertEquals(sye_pre.getStackTrace().length, sye_post.getStackTrace().length);
        Assert.assertEquals(re_pre.getStackTrace().length, re_post.getStackTrace().length);
        Assert.assertEquals(he1_pre.getStackTrace().length, he1_post.getStackTrace().length);
        Assert.assertEquals(e_pre.getStackTrace().length, e_post.getStackTrace().length);
    }

}
