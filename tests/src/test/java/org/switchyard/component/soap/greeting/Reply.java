/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
