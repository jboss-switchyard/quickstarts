package org.switchyard.quickstarts.demo.txpropagation;

import javax.inject.Inject;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service(CreditCheckService.class)
public class CreditCheckServiceBean implements CreditCheckService {

    @Inject @Reference
    private RuleService checkRule;

    @Inject @Reference
    private ApplicationLogger applicationLogger;
    
    @Override
    public Application checkCredit(Offer offer) {
        Application app = checkRule.checkCredit(offer);
        applicationLogger.store(app);
        return app;
    }

}
