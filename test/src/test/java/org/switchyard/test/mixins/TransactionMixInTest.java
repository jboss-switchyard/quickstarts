/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.test.mixins;

import static org.junit.Assert.assertSame;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for {@link TransactionMixIn}.
 * 
 * @author Lukasz Dywicki
 */
public class TransactionMixInTest {

    private static TransactionMixIn mixin;

    @BeforeClass
    public static void setup() {
        mixin = new TransactionMixIn();
        mixin.initialize();
    }
    
    @AfterClass
    public static void tearDown() {
        mixin.uninitialize();
    }

    @Test
    public void testTransaction() throws Exception {
        UserTransaction transaction = getUserTransaction();
        transaction.begin();

        transaction.commit();
    }

    @Test
    public void shouldUseSameInstance() throws Exception {
        assertSame(getUserTransaction(), mixin.getUserTransaction());
    }

    private UserTransaction getUserTransaction() throws NamingException {
        return (UserTransaction) new InitialContext().lookup("java:jboss/UserTransaction");
    }

}
