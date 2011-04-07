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

package org.switchyard.deployment.torquebox;

import org.jruby.RubyHash;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ServiceInvokerTest {

    @Test
    public void test_deepClone() {
        RubyHash rubyHash = (RubyHash) RubyUtil.evalScriptlet(getClass().getResourceAsStream("/order1.rb"));
        Map<String,Object> deepClone = ServiceInvoker.deepClone(rubyHash);

        Map header = (Map) deepClone.get("header");
        Assert.assertFalse(header instanceof IRubyObject);
        Assert.assertEquals(1234L, header.get("orderId"));

        List items = (List) deepClone.get("items");
        Assert.assertFalse(items instanceof IRubyObject);
        Assert.assertEquals(2, items.size());
    }
}
