package org.switchyard.quickstarts.demo.txpropagation;

import org.switchyard.component.bean.Service;

@Service(RuleService.class)
public class RuleServiceBean implements RuleService {

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
