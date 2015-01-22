package org.switchyard.quickstarts.remoteinvoker;

import org.switchyard.component.bean.Service;

@Service(CreditCheck.class)
public class CreditCheckBean implements CreditCheck {

    @Override
    public Application checkCredit(Offer offer) {
        if (offer.getApplication().getCreditScore() >= 600) {
            offer.getApplication().setApproved(true);
        } else {
            offer.getApplication().setApproved(false);
        }
        return offer.getApplication();
    }

}
