/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.internal.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;
import org.switchyard.internal.DefaultContext;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.internal.ExchangeImpl;
import org.switchyard.internal.io.Data.Car;
import org.switchyard.internal.io.Data.Person;
import org.switchyard.metadata.ExchangeContract;

/**
 * Tests de/serialization of a Context, Message and Exchange, as well as pertinent objects reachable in the graph.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class ExchangeSerializationTests {

    private static final Serializer SERIALIZER = SerializerType.DEFAULT.instance();

    private static final <T> T serDeser(T object, Class<T> clazz) throws Exception {
        byte[] bytes = SERIALIZER.serialize(object, clazz);
        return SERIALIZER.deserialize(bytes, clazz);
    }

    @Test
    public void testContextSerialization() throws Exception {
        DefaultContext ctx = buildContext(null);
        ctx = serDeser(ctx, DefaultContext.class);
        assertContext(ctx);
    }

    @Test
    public void testMessageSerialization() throws Exception {
        DefaultMessage msg = buildMessage(null);
        msg = serDeser(msg, DefaultMessage.class);
        assertMessage(msg);
    }

    @Test
    @Ignore
    public void testExchangeSerialization() throws Exception {
        ExchangeImpl exchange = buildExchange();
        exchange = serDeser(exchange, ExchangeImpl.class);
        assertExchange(exchange);
    }

    private DefaultContext buildContext(DefaultContext ctx) {
        if (ctx == null) {
            ctx = new DefaultContext();
        }
        ctx.setProperty("foo", "bar");
        ctx.setProperty("car", new Car(new Person("driver")));
        return ctx;
    }

    private DefaultMessage buildMessage(DefaultMessage msg) {
        if (msg == null) {
            msg = new DefaultMessage();
        }
        msg.setContent("content");
        msg.addAttachment("data", new MockDataSource("mock", "text/plain", "abc123"));
        return msg;
    }

    private ExchangeImpl buildExchange() {
        MockDomain domain = new MockDomain();
        MockHandler handler = new MockHandler();
        ServiceReference service = domain.createInOutService(new QName("InPhase"), handler);
        ExchangeImpl exchange = (ExchangeImpl)service.createExchange(handler);
        buildContext((DefaultContext)exchange.getContext());
        DefaultMessage msg = buildMessage((DefaultMessage)exchange.createMessage());
        exchange.send(msg);
        handler.waitForOKMessage();
        return exchange;
    }

    private void assertContext(Context ctx) throws Exception {
        Assert.assertEquals("bar", ctx.getProperty("foo").getValue());
        Assert.assertEquals("driver", ((Car)ctx.getProperty("car").getValue()).getDriver().getName());
    }

    private void assertMessage(Message msg) throws Exception {
        Assert.assertEquals("content", msg.getContent());
        InputStream is =  msg.getAttachment("data").getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[6];
        int read = 0;
        while ((read = is.read(buff)) != -1) {
            baos.write(buff, 0, read);
        }
        Assert.assertEquals("abc123", new String(baos.toByteArray()));
    }

    private void assertExchange(Exchange exchange) throws Exception {
        assertContext(exchange.getContext());
        assertMessage(exchange.getMessage());
        Assert.assertEquals(ExchangeContract.IN_ONLY, exchange.getContract());
        Assert.assertEquals(ExchangePhase.IN, exchange.getPhase());
        Assert.assertEquals(ExchangeState.OK, exchange.getState());
    }

    private static final class MockDataSource implements DataSource {
        private String _name;
        private String _contentType;
        private String _content;
        private MockDataSource(String name, String contentType, String content) {
            _name = name;
            _contentType = contentType;
            _content = content;
        }
        public String getName() {
            return _name;
        }
        public String getContentType() {
            return _contentType;
        }
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(_content.getBytes());
        }
        public OutputStream getOutputStream() throws IOException {
            return null;
        }
    }

}
