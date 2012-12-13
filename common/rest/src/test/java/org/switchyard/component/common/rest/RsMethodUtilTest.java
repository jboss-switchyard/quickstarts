/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.common.rest;

import java.util.Map;

import javax.activation.DataSource;
import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceOperation;

/**
 * Tests for REST utility.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RsMethodUtilTest {

    private static final String RESOURCES = "org.switchyard.component.common.rest.support.WarehouseResource, org.switchyard.component.common.rest.support.OrderResource";
    private static final String GET_ITEM = "getItem : IN_OUT : [java:java.lang.Integer, java:org.switchyard.component.common.rest.support.Item, null]";
    private static final String GET_ITEM_COUNT = "getItemCount : IN_OUT : [null, java:java.lang.Integer, null]";
    private static final String ADD_ITEMS = "addItems : IN_OUT : [java:org.switchyard.component.common.rest.support.Order, java:java.lang.String, null]";
    private static final String REMOVE_ITEM = "removeItem : IN_OUT : [java:java.lang.Integer, java:java.lang.String, java:java.lang.Exception]";
    private static final String NEW_ORDER = "newOrder : IN_OUT : [null, java:org.switchyard.component.common.rest.support.Order, null]";
    private static final String ADD_ITEM = "addItem : IN_OUT : [java:org.switchyard.component.common.rest.support.Item, java:java.lang.String, java:java.lang.Exception]";
    private static final String UPDATE_ITEM = "updateItem : IN_OUT : [java:org.switchyard.component.common.rest.support.Item, java:java.lang.String, java:java.lang.Exception]";
    private static final String REMOVE_ORDER_ITEM = "removeItem : IN_OUT : [java:java.lang.String, java:java.lang.String, null]";
    private static final String GET_ORDER = "getOrder : IN_OUT : [java:java.lang.Integer, java:org.switchyard.component.common.rest.support.Order, null]";

    @Test
    public void parseResources() throws Exception {
        Map<String, RsMethod> parsedResources = RsMethodUtil.parseResources(RESOURCES);
        System.out.println(parsedResources);
        Assert.assertNotNull(parsedResources);
        Assert.assertTrue(parsedResources.containsKey(GET_ITEM));
        RsMethod method = parsedResources.get(GET_ITEM);
        Assert.assertTrue(method.getProduces().contains(MediaType.APPLICATION_XML_TYPE));
        Assert.assertTrue(method.getProduces().contains(MediaType.APPLICATION_JSON_TYPE));
        Assert.assertTrue(method.getConsumes().contains(MediaType.TEXT_PLAIN_TYPE));
        Assert.assertEquals("/warehouse/item/param", RsMethodUtil.getPath(method, new MockExchange()));

        Assert.assertTrue(parsedResources.containsKey(GET_ITEM_COUNT));
        method = parsedResources.get(GET_ITEM_COUNT);
        Assert.assertTrue(method.getProduces().contains(MediaType.TEXT_PLAIN_TYPE));
        Assert.assertTrue(method.getConsumes().contains(MediaType.TEXT_PLAIN_TYPE));
        Assert.assertEquals("/warehouse/count/", RsMethodUtil.getPath(method, new MockExchange()));

        Assert.assertTrue(parsedResources.containsKey(ADD_ITEMS));
        method = parsedResources.get(ADD_ITEMS);
        Assert.assertTrue(method.getProduces().contains(MediaType.APPLICATION_XML_TYPE));
        Assert.assertTrue(method.getConsumes().contains(MediaType.APPLICATION_XML_TYPE));
        Assert.assertEquals("/order/item", RsMethodUtil.getPath(method, new MockExchange()));

        Assert.assertTrue(parsedResources.containsKey(REMOVE_ITEM));
        method = parsedResources.get(REMOVE_ITEM);
        Assert.assertTrue(method.getProduces().contains(MediaType.TEXT_PLAIN_TYPE));
        Assert.assertTrue(method.getConsumes().contains(MediaType.TEXT_PLAIN_TYPE));
        Assert.assertEquals("/warehouse?itemId=param", RsMethodUtil.getPath(method, new MockExchange()));

        Assert.assertTrue(parsedResources.containsKey(NEW_ORDER));
        method = parsedResources.get(NEW_ORDER);
        Assert.assertEquals("/order/", RsMethodUtil.getPath(method, new MockExchange()));

        Assert.assertTrue(parsedResources.containsKey(ADD_ITEM));
        method = parsedResources.get(ADD_ITEM);
        Assert.assertEquals("/warehouse/", RsMethodUtil.getPath(method, new MockExchange()));

        Assert.assertTrue(parsedResources.containsKey(UPDATE_ITEM));
        method = parsedResources.get(UPDATE_ITEM);
        Assert.assertEquals("/warehouse/", RsMethodUtil.getPath(method, new MockExchange()));

        Assert.assertTrue(parsedResources.containsKey(REMOVE_ORDER_ITEM));
        method = parsedResources.get(REMOVE_ORDER_ITEM);
        Assert.assertEquals("/order;itemId=param", RsMethodUtil.getPath(method, new MockExchange()));

        Assert.assertTrue(parsedResources.containsKey(GET_ORDER));
        method = parsedResources.get(GET_ORDER);
        Assert.assertEquals("/order/param", RsMethodUtil.getPath(method, new MockExchange()));
    }

    private class MockExchange implements Exchange {

        public Context getContext() {
            return null;
        }

        public ExchangeContract getContract() {
            return null;
        }

        public Message getMessage() {
            return new MockMessage();
        }

        public Message createMessage() {
            return null;
        }

        public void send(Message message) {
            throw new IllegalStateException("Unexpected");
        }

        public void sendFault(Message message) {
            throw new IllegalStateException("Unexpected");
        }

        public ExchangeState getState() {
            return null;
        }

        public ExchangePhase getPhase() {
            return null;
        }

        @Override
        public ServiceReference getConsumer() {
            return null;
        }

        @Override
        public Service getProvider() {
            return null;
        }

        @Override
        public Exchange consumer(ServiceReference consumer, ServiceOperation operation) {
            return null;
        }

        @Override
        public Exchange provider(Service provider, ServiceOperation operation) {
            return null;
        }
    }

    private class MockMessage implements Message {

        public Message setContent(Object content) {
            return null;
        }

        public Object getContent() {
            return "param";
        }

        public <T> T getContent(Class<T> type) {
            return null;
        }

        public Message addAttachment(String name, DataSource attachment) {
            return null;
        }

        public DataSource getAttachment(String name) {
            return null;
        }

        public DataSource removeAttachment(String name) {
            return null;
        }

        public Map<String, DataSource> getAttachmentMap() {
            return null;
        }
    }
}
