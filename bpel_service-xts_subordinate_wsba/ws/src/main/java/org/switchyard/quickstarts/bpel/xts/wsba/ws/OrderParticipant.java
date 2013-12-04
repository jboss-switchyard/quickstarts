package org.switchyard.quickstarts.bpel.xts.wsba.ws;

import java.io.Serializable;
import java.util.logging.Logger;

import com.arjuna.wst.BusinessAgreementWithCoordinatorCompletionParticipant;
import com.arjuna.wst.FaultedException;
import com.arjuna.wst.SystemException;
import com.arjuna.wst.WrongStateException;

/**
 * 
 * Order participant to prepare and order fly tickets.
 * 
 */
public class OrderParticipant implements BusinessAgreementWithCoordinatorCompletionParticipant, Serializable {

    private static final long serialVersionUID = -6459800278322126331L;

    private static Logger log = Logger.getLogger(OrderParticipant.class
            .getName());

    private String _txID;
    private String _name;
    private String _fltid;

    /**
     * @param txID transaction identifier
     * @param name username
     * @param fltid flight identifier
     */
    public OrderParticipant(String txID, String name, String fltid) {
        _txID = txID;
        _name = name;
        _fltid = fltid;
    }

    /**
     * Get transaction identifier.
     * @return transaction identifier
     */
    public String getTxID() {
        return _txID;
    }

    @Override
    public void unknown() throws SystemException {

    }

    @Override
    public void error() throws SystemException {

    }

    @Override
    public void cancel() throws FaultedException, WrongStateException,
            SystemException {
        log.info("\n================================================================================\n"
                + "AirportOrderParticipant "
                + _fltid
                + " cancel"
                + "\n================================================================================");
    }

    @Override
    public void close() throws WrongStateException, SystemException {
        log.info("\n================================================================================\n"
                + "AirportOrderParticipant "
                + _fltid
                + " close"
                + "\n================================================================================");
    }

    @Override
    public void compensate() throws FaultedException, WrongStateException,
            SystemException {
        log.info("\n================================================================================\n"
                + "AirportOrderParticipant "
                + _fltid
                + " compensate"
                + "\n================================================================================");
    }

    @Override
    public String status() throws SystemException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void complete() throws WrongStateException, SystemException {
        log.info("\n================================================================================\n"
                + "AirportOrderParticipant "
                + _fltid
                + " complete"
                + "\n================================================================================");
    }

}
