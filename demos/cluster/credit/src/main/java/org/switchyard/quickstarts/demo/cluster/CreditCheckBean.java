package org.switchyard.quickstarts.demo.cluster;

import org.jboss.logging.Logger;
import org.switchyard.component.bean.Service;

@Service(CreditCheck.class)
public class CreditCheckBean implements CreditCheck {

    private Logger _log = Logger.getLogger(CreditCheckBean.class);

    @Override
    public Application checkCredit(Offer offer) {
        if (offer.getApplication().getCreditScore() >= 600) {
            offer.getApplication().setApproved(true);
            _log.info("Credit Service : Approving credit for " + offer.getApplication().getName());
        } else {
            offer.getApplication().setApproved(false);
            _log.info("Credit Service : Denying credit for " + offer.getApplication().getName());
        }
        return offer.getApplication();
    }

}
