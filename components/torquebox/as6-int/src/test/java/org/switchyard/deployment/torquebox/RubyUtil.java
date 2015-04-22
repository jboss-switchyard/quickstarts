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

import org.jruby.Ruby;
import org.jruby.ast.Node;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.InputStream;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class RubyUtil {

    public static IRubyObject evalScriptlet(InputStream scriptStream) {
        Ruby ruby = Ruby.newInstance();
        Node scriptNode = ruby.parseFromMain(scriptStream, "test.rb");
        return ruby.runInterpreter(scriptNode);
    }
}
