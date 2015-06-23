/**
 * 
 */
package edu.neu.ccis.sms.constants;

/**
 * Interface containing keys for error messages displayed within the application
 *
 * @author Swapnil Gupta
 * @version SMS 1.0
 * @since June 12, 2015
 */
public interface ErrorMessageKeys {

    public static final String MEMBER_EXISTS_FOR_PARENT = "Member with same name already exist for its parent. Use a different name.";
    public static final String UNAUTHORIZED_MEMBER_CREATION = "You need to have the conductor role on the parent member to create new member";
    public static final String PARENT_MEMBER_INACTIVE =  "Parent is inactive, activate it first.";
    public static final String NO_USER_WITH_EMAIL =  "No user with this email found.";
    public static final String USER_ALREADY_HAS_ROLE_FOR_MEMBER =  "You already have this role for the member";
    public static final String OTHER_USER_ALREADY_HAS_ROLE_FOR_MEMBER =  "The user is already have this role for the member";
}
