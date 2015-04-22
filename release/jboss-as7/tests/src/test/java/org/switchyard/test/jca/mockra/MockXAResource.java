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
package org.switchyard.test.jca.mockra;

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
