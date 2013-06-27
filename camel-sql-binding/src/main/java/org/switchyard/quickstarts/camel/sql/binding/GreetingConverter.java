/*
 * 2012 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.quickstarts.camel.sql.binding;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.camel.Converter;

/**
 * Custom type converter used to convert from {@link Greeting} into {@link Iterator}.
 */
@Converter
public class GreetingConverter {

    /**
     * Wraps greeting into iterator.
     * 
     * @param greeting Greeting.
     * @return
     */
    @Converter
    public static Iterator<Object> from(Greeting greeting) {
        return new PojoIterator(greeting);
    }

    @Converter
    public static Greeting[] from(List<Map<String, Object>> objects) {
        Greeting[] greetings = new Greeting[objects.size()];
        int position = 0;
        for (Map<String, Object> greeting : objects) {
            greetings[position++] = new Greeting(
                (Integer) greeting.get("id"),
                (String) greeting.get("receiver"),
                (String) greeting.get("sender")
            );
        }
        return greetings;
    }

}
