/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package org.switchyard.quickstarts.demo.cluster;

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
        System.out.println("Dealer Service : Received an offer");
        Deal deal = new Deal();
        deal.setOffer(offer);
        
        // If the offer is more than 10% off, then reject it
        if (offer.getCar().getPrice() * .9 > offer.getAmount()) {
            deal.setAccepted(false);
            return deal;
        }

        System.out.println("Dealer Service : Checking Credit");
        // Check credit of applicant
        Application creditReply = creditService.checkCredit(offer);
        deal.setAccepted(creditReply.isApproved());
        return deal;
    }

}
