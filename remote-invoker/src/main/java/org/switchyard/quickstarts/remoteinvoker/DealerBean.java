package org.switchyard.quickstarts.remoteinvoker;

import javax.inject.Inject;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service(Dealer.class)
public class DealerBean implements Dealer {

    @Inject
    @Reference
    private CreditCheck creditService;

    @Override
    public Deal offer(Offer offer) {
        Deal deal = new Deal();
        deal.setOffer(offer);

        // If the offer is more than 10% off, then reject it
        if (offer.getCar().getPrice() * .9 > offer.getAmount()) {
            deal.setAccepted(false);
            return deal;
        }

        // Check credit of applicant
        Application creditReply = creditService.checkCredit(offer);
        deal.setAccepted(creditReply.isApproved());
        return deal;
    }

}
