package org.switchyard.component.test.mixins.jca;

import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;

/**
 * interaction event listener interface.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public interface InteractionListener {
    /**
     * this is invoked from Interaction.execute(InteractionSpec ispec, Record input, Record output).
      * 
     * @param ispec InteractionSpec
     * @param input input record
     * @param output output record
     * @return true if succeeded
     */
    public boolean onExecute(InteractionSpec ispec, Record input, Record output);
    
    /**
     * this is invoked from Interaction.execute(InteractionSpec ispec, Record input).
     * 
     * @param ispec InteractionSpec
     * @param input input record
     * @return output record
     */
    public Record onExecute(InteractionSpec ispec, Record input);
}
