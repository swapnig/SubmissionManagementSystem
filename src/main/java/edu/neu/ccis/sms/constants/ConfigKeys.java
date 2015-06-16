package edu.neu.ccis.sms.constants;

/**
 * Interface containing keys for all the properties/attributes referenced from
 * the configuration files. All these keys need to be present in the
 * configuration file.
 * 
 * For proper documentation any new properties/attributes should be updated
 * here. Any property/attribute change in the configuration file should be
 * reflected here for proper functioning of the SMS
 *
 * @author Swapnil Gupta
 * @version SMS 1.0
 * @since May 18, 2015
 */
public interface ConfigKeys {

	/** Key for the root property containing all the roles for the system. */
	public static final String ROLES_ROOT_ELEMENT = "roles";

	/** Key for the root category of the system. */
	public static final String HIERARCHY_ROOT_ELEMENT = "category";

	/**
	 * Key for all the subsequent child categories qualified as
	 * parentcategory.HIERARCHY_CHILD_ELEMENT
	 */
	public static final String HIERARCHY_CHILD_ELEMENT = "subCategory";

	/**
	 * Key for the delimiter used to qualify parent child relationship. e,g
	 * parent.child. Here '.' is used as the delimiter
	 */
	public static final String HIERARCHY_ELEMENT_DELIMITER = ".";

	/** Key for the root member of an individual application instance */
	public static final String HIERARCHY_ROOT_MEMBER_ELEMENT = "member";

	/** Key for the username(user) with the conductor privilege on root member of an
	 * individual application instance */
	public static final String HIERARCHY_ROOT_USERNAME_ELEMENT = "username";

	/** Key for the password(user) with the conductor privilege on root member of an
	 * individual application instance */
	public static final String HIERARCHY_ROOT_PASSWORD_ELEMENT = "password";

	/** Key for the firstname(user) with the conductor privilege on root member of an
	 * individual application instance */
	public static final String HIERARCHY_ROOT_FIRSTNAME_ELEMENT = "firstname";

	/** Key for the email(user) with the conductor privilege on root member of an
	 * individual application instance */
	public static final String HIERARCHY_ROOT_EMAIL_ELEMENT = "email";

	/**
	 * Key for the root property containing all the member attributes (html
	 * labels) of a given category.
	 */
	public static final String CATEGORY_ELEMENT_HTML_LABELS = "labels";

	/** Key for the attribute containing the value for category name. */
	public static final String CATEGORY_ATTRIBUTE_NAME = "name";

	/** Key for the attribute describing whether the category is registrable */
	public static final String CATEGORY_ATTRIBUTE_REGISTRABLE = "registrable";

	/** Key for the attribute describing whether the category is submittable */
	public static final String CATEGORY_ATTRIBUTE_SUBMITTABLE = "submittable";
}
