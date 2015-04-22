package org.switchyard.test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation to indicate MixIn dependencies.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface MixInDependencies {

    /**
     * A list of mixins which must be initialized before the annotated mixin does.
     * These mixins will be instantiated by SwitchYardTestKit even if they are not on the
     * mixins list of {@link SwitchYardTestCaseConfig} annotation.
     */
    Class<? extends TestMixIn>[] required() default SwitchYardTestKit.NullMixIns.class;
    
    /**
     * A list of mixins which should be initialized before the annotated mixin does
     * if they will be used. These mixins will not be instantiated unless they are on the
     * mixins list of {@link SwitchYardTestCaseConfig} annotation.
     * This option can be used to handle the case like that - the MixInA doesn't always need
     * MixInB, but when we use MixInA and MixInB together, MixInB should be initialized first.
     * In this case the MixInA should have a optional dependency on MixInB.
     */
    Class<? extends TestMixIn>[] optional() default SwitchYardTestKit.NullMixIns.class;
}
