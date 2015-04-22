/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
