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
package org.switchyard.test.mixins.jca;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.log4j.Logger;

/**
 * MockXAResource.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class MockXAResource implements XAResource {
    private Logger _logger = Logger.getLogger(MockXAResource.class);
    
    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        _logger.debug("call commit(" + xid + ", " + onePhase + ")");
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        _logger.debug("call end(" + xid + ", " + flags + ")");
    }

    @Override
    public void forget(Xid xid) throws XAException {
        _logger.debug("call forget(" + xid + ")");
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        _logger.debug("call getTransactionTimeout");
        return 0;
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        return xares.equals(this);
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        _logger.debug("call prepare(" + xid + ")");
        return 0;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        _logger.debug("call recover(" + flag + ")");
        return null;
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        _logger.debug("call rollback(" + xid + ")");
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        _logger.debug("call setTransactionTimeout(" + seconds + ")");
        return false;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        _logger.debug("call start(" + xid + ", " + flags + ")");
    }
    
}
