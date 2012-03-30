package org.switchyard.test.mixins.jca;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * JCAMixIn specific configuration for the SwitchYard testcase.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface JCAMixInConfig {

    /**
     * Name of MockResourceAdapter.
     */
    String mockResourceAdapter() default "";
    
    /**
     * Name of HornetQ RAR.
     */
    String hornetQResourceAdapter() default "";
    
    /**
     * Resource Adapters to deploy.
     */
    String[] resourceAdapters() default {};
}
