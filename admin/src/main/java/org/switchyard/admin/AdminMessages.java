package org.switchyard.admin;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * <p/>
 * This file is using the subset 10400-10599 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface AdminMessages {
    /**
     * The default messages.
     */
    AdminMessages MESSAGES = Messages.getBundle(AdminMessages.class);

    /**
     * messageMetricsString method definition.
     * @param successCount successCount 
     * @param faultCount faultCount
     * @param totalCount totalCount 
     * @param averageProcessingTime averageProcessingTime
     * @param minProcessingTime minProcessingTime
     * @param maxProcessingTime maxProcessingTime
     * @param totalProcessingTime totalProcessingTime
     * @return localized message
     */
    @Message(id = 10400, value = "Success Count : %s " 
            + "Fault Count   : %s" 
            + "Total Count   : %s"
            + "Avg Time MS   : %s"
            + "Min Time MS   : %s"
            + "Max Time MS   : %s"
            + "Total Time MS : %s")
    String messageMetricsString(String successCount, String faultCount, String totalCount,
            String averageProcessingTime, String minProcessingTime, String maxProcessingTime,
            String totalProcessingTime);

}
