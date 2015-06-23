package edu.neu.ccis.sms.constants;

/**
 * Constant Keys for attributes stored in HttpSession
 * 
 * @author Pramod Khare
 * @date 4-June-2015
 * @lastUpdate 10-June-2015
 */
public interface SessionKeys {
    /** Current logged in user's username */
    public static final String keyUserName = "USER_NAME";
    /** Current logged in user's userid */
    public static final String keyUserId = "USER_ID";
    /** Stores the current logged in user's details */
    public static final String keyUserObj = "USER_OBJ";
    /** Stores the User registrations mappings to members */
    public static final String keyUserMemberMappings = "USER_TO_MEMBER_MAPPINGS";
    /** Active Member Id - i.e. A registerable member which is currently getting accessed e.g. Course, section */
    public static final String activeMemberId = "ACTIVE_MEMBER_ID";
    /** Active submittable member Id e.g. assignment, project */
    public static final String activeSubmittableMemberId = "SUBMITTABLE_ACTIVE_MEMBER_ID";
}
