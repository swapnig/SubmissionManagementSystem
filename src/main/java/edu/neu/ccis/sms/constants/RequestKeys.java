package edu.neu.ccis.sms.constants;

/**
 * Constant Keys for HttpServletRequest attributes
 * 
 * @author Pramod Khare
 * @date 4-June-2015
 * @ModifiedBy Swapnil Gupta
 * @lastUpdate 20-June-2015
 */

public interface RequestKeys {

    /** Attribute identifying conductor role for the member */
    public static final String PARAM_ROLE_CONDUCTOR = "conductor";

    /** Attribute uniquely identifying a member */
    public static final String PARAM_MEMBER_ID = "memberId";

    /** Attribute identifying name of current member */
    public static final String PARAM_MEMBER_NAME = "memberName";

    /** Attribute identifying member attributes passed as a request parameter */
    public static final String PARAM_MEMBER_ATTRIBUTES = "memberAttributes";

    /** Attribute identifying the member id of the parent of the current member */
    public static final String PARAM_PARENT_MEMBER_ID = "parentMemberId";

    /** Attribute identifying the member name of the parent of the current member */
    public static final String PARAM_PARENT_MEMBER_NAME = "parentMemberName";

    /** Attribute identifying the category of the member to be created */
    public static final String PARAM_CATEGORY_NAME = "category";

    /** Attribute identifying the current role or role to be assigned*/
    public static final String PARAM_USER_ROLE = "role";

    /** Attribute identifying a user email id*/
    public static final String PARAM_USER_EMAIL = "userEmailId";

    /** message attribute stored in request while forwarding it */
    public static final String PARAM_MESSAGE = "message";
}