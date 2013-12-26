package org.switchyard.test.jca;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service(JCAJMSTransactionService.class)
public class JCAJMSTransactionServiceImpl implements JCAJMSTransactionService {

    private Logger _logger = Logger.getLogger(JCAJMSTransactionServiceImpl.class);

    @Inject @Reference
    private JCAJMSReferenceService service;
    @Inject @Reference("JCAJMSCamelService")
    private JCAJMSReferenceService camelService;

    @Override
    public void onMessage(String name) {
        int txStatus = getTransactionStatus();
        if (txStatus != Status.STATUS_NO_TRANSACTION) {
            throw new RuntimeException("Unexpected Transaction Status: " + txStatus);
        }
        service.onMessage(name);
    }

    @Override
    public void onMessageText(String name) {
        int txStatus = getTransactionStatus();
        if (txStatus != Status.STATUS_NO_TRANSACTION) {
            throw new RuntimeException("Unexpected Transaction Status: " + txStatus);
        }
        service.onMessageText(name);
    }

    @Override
    public void onMessageManaged(String name) {
        int txStatus = getTransactionStatus();
        if (txStatus != Status.STATUS_ACTIVE) {
            throw new RuntimeException("Unexpected Transaction Status: " + txStatus);
        }
        service.onMessage(name);
    }

    @Override
    public void onMessageCamel(String name) {
        int txStatus = getTransactionStatus();
        if (txStatus != Status.STATUS_ACTIVE) {
            throw new RuntimeException("Unexpected Transaction Status: " + txStatus);
        }
        camelService.onMessage(name);
    }

    @Override
    public void onMessageContextProperty(String name) throws Exception {
        int txStatus = getTransactionStatus();
        if (txStatus != Status.STATUS_ACTIVE) {
            throw new RuntimeException("Unexpected Transaction Status: " + txStatus);
        }
        service.onMessageContextProperty(name);
    }

    private int getTransactionStatus() {
        try {
            InitialContext ic = new InitialContext();
            UserTransaction tx = (UserTransaction) ic.lookup("java:jboss/UserTransaction");
            if (tx != null) {
                return tx.getStatus();
            }
        } catch (Exception e) {
            _logger.warn(e);
        }
        return Status.STATUS_NO_TRANSACTION;
    }
}

