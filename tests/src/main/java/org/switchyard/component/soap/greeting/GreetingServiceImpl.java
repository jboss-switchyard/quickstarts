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

package org.switchyard.component.soap.greeting;

import org.switchyard.component.bean.Service;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Service(GreetingService.class)
public class GreetingServiceImpl implements GreetingService {

    private static final int GREETID = 987789;

    @Override
    public Reply greet(Greeting greeting) throws GreetingServiceException {
        System.out.println("\t****Greetings from " + greeting.getPerson() + " at " + greeting.getTime());

        if ("throwme".equals(greeting.getPerson().getFirstName())) {
            throw new GreetingServiceException("Application Exception from GreetingService !!");
        }

        return new Reply().setGreetingId(GREETID).setPerson(greeting.getPerson());
    }
}
