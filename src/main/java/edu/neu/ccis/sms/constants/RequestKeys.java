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
	
	public static final String PARAM_ROLE_CONDUCTOR = "conductor";
	public static final String PARAM_MEMBER_ID = "memberId";
	public static final String PARAM_MEMBER_NAMEE = "memberName";
	public static final String PARAM_MEMBER_ATTRIBUTES = "memberAttributes";
	public static final String PARAM_PARENT_MEMBER_ID = "parentMemberId";
	public static final String PARAM_PARENT_MEMBER_NAME = "parentMemberName";
	public static final String PARAM_CATEGORY_NAME = "category";
	public static final String PARAM_MEMBER_NAME = "Name";
	public static final String PARAM_USER_ROLE = "role";
	public static final String PARAM_USER_EMAIL = "userEmailId";

    /** message attribute stored in request while forwarding it */
    public static final String PARAM_MESSAGE = "message";
}