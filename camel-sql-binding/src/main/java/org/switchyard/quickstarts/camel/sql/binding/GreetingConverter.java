/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
