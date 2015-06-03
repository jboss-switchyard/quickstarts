/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
