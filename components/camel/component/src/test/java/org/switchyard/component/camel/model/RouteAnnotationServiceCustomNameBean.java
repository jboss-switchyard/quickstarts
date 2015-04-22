package org.switchyard.component.camel.model;

import org.switchyard.component.camel.Route;

/**
 * @author: <a href="mailto:eduardo.devera@gmail.com">Eduardo de Vera</a>
 * Date: 7/13/12
 * Time: 12:36 AM
 */
@Route(value = RouteAnnotationService.class, name="CustomRouteAnnotationServiceName")
public class RouteAnnotationServiceCustomNameBean implements RouteAnnotationService {

    @Override
    public void noOp(String ignored) {}
}
