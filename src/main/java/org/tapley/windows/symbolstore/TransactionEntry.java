package org.tapley.windows.symbolstore;

import java.time.LocalDateTime;

public class TransactionEntry {
    private String transactionId;
    private LocalDateTime insertingTime;
    private String applicationName;
    private String applicationVersion;
    private String comment;
    
    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @param applicationName the applicationName to set
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * @return the insertingTime
     */
    public LocalDateTime getInsertingTime() {
        return insertingTime;
    }

    /**
     * @param insertingTime the insertingTime to set
     */
    public void setInsertingTime(LocalDateTime insertingTime) {
        this.insertingTime = insertingTime;
    }

    /**
     * @return the applicationVersion
     */
    public String getApplicationVersion() {
        return applicationVersion;
    }

    /**
     * @param applicationVersion the applicationVersion to set
     */
    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
    
}
