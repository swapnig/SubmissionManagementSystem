package edu.neu.ccis.sms.constants;

/**
 * Keys for attributes stored in HttpSession
 * 
 * @author Pramod Khare
 * @date 4-June-2015
 * @lastUpdate 10-June-2015
 */
public interface SessionKeys {
    public static final String keyUserName = "USER_NAME";
    public static final String keyUserId = "USER_ID";
    public static final String keyUserObj = "USER_OBJ";

    /* Active Member Id - i.e. Member which is currently getting accessed */
    public static final String activeMemberId = "ACTIVE_MEMBER_ID";

    public static final String activeSubmittableMemberId = "SUBMITTABLE_ACTIVE_MEMBER_ID";

    public static final String keyUserMemberMappings = "USER_TO_MEMBER_MAPPINGS";
}
