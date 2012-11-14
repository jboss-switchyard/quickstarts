package org.switchyard.quickstarts.camel.amqp.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.switchyard.component.bean.Service;

/**
 * @author: <a href="mailto:eduardo.devera@gmail.com">Eduardo de Vera</a>
 * Date: 11/11/12
 * Time: 4:46 PM
 */
@Service(GreetingService.class)
public class GreetingServiceBean implements GreetingService {

    private static final Log LOG = LogFactory.getLog(GreetingServiceBean.class.getSimpleName());

    @Override
    public void greet(String name) {
        LOG.info("Hola " + name + " ;-) ");
    }


}
