package edu.neu.ccis.sms.entity.users;

/**
 * Enum for User registration status for a member
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 */
public enum StatusType {
    /** Failed registration or when registration is accepted or invalid */
    NOPASS,

    /** User is in waiting list for this member registration */
    WAITING,

    /** Accepted - or successful registration */
    ACTIVE;
}
