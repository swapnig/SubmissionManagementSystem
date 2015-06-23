package edu.neu.ccis.sms.entity.users;

/**
 * Enum for User's enrollment status for a Member
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 */
public enum RoleType {
    /** Super administrator Role - has access to everything - should only be used for superusers */
    ADMIN,

    /** Conductor of a member */
    CONDUCTOR,

    /** Evaluator of a member who evaluates the submissions from all submitters */
    EVALUATOR,

    /** Submitter who submits the submission for a member */
    SUBMITTER
}
