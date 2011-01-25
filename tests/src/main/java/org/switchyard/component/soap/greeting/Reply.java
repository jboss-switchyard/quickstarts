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

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Reply {

    private int _greetingId;
    private Person _person;

    /**
     * @return _greetingId
     */
    public int getGreetingId() {
        return _greetingId;
    }

    /**
     * @param greetingId greetingId
     * @return _greetingId
     */
    public Reply setGreetingId(int greetingId) {
        this._greetingId = greetingId;
        return this;
    }

    /**
     * @return _person
     */
    public Person getPerson() {
        return _person;
    }

    /**
     * @param person person
     * @return _person
     */
    public Reply setPerson(Person person) {
        this._person = person;
        return this;
    }
}
