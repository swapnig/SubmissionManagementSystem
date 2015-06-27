/**
 * 
 */
package edu.neu.ccis.sms.constants;

/**
 * Interface containing keys for error messages displayed within the application
 *
 * @author Swapnil Gupta
 * @date 11-June-2015
 * @lastUpdate 20-June-2015
 */
public interface ErrorMessageKeys {

    /**
     * Error message to be displayed when an attempt is made to create a
     * member with a name that already exists for the parent member
     */
    public static final String MEMBER_EXISTS_FOR_PARENT =
            "Member with same name already exist for its parent. Use a different name.";

    /**
     * Error message to be displayed when an attempt is made to create a
     * member while not having conductor access on the parent member
     */
    public static final String UNAUTHORIZED_MEMBER_CREATION =
            "You need to have the conductor role on the parent member to create new member";

    /**
     * Error message to be displayed when an attempt is made to activate
     * a member whose parent is not currently active
     */
    public static final String PARENT_MEMBER_INACTIVE =  "Parent is inactive, activate it first.";

    /**
     * Error message to be displayed when an attempt is made to assign
     * a role to a user, whoose email id does not exist in the system
     */
    public static final String NO_USER_WITH_EMAIL =  "No user with this email found.";

    /**
     * Error message to be displayed when an attempt is made to request
     * a role that they already have on the member
     */
    public static final String USER_ALREADY_HAS_ROLE_FOR_MEMBER =  "You already have this role for the member";

    /**
     * Error message to be displayed when an attempt is made to request
     * a role for another user that already has that role on the member
     */
    public static final String OTHER_USER_ALREADY_HAS_ROLE_FOR_MEMBER =  "The user is already have this role for the member";
}
