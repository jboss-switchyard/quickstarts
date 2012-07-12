package org.switchyard.component.camel.tests;

import org.switchyard.component.camel.Route;

/**
 * @author: <a href="mailto:eduardo.devera@gmail.com">Eduardo de Vera</a>
 * Date: 7/13/12
 * Time: 12:35 AM
 */
@Route(value = RouteAnnotationService.class)
public class RouteAnnotationServiceEmptyNameBean implements RouteAnnotationService {

    @Override
    public void noOp(String ignored) {}
}
