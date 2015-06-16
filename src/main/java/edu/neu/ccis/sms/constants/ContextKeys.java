package edu.neu.ccis.sms.constants;

/**
 * Interface containing keys for all the attributes stored in the application
 * context
 *
 * @author Swapnil Gupta
 * @version SMS 1.0
 * @since May 18, 2015
 */
public interface ContextKeys {

	/**
	 * Key for attribute containing Configuration object containing the entire
	 * terminology for an individual application context
	 */
	public static final String TERMINOLOGY_CONFIG = "terminologyConfig";

	/**
	 * Key for attribute containing category mapped to its property key in the
	 * terminology configuration.
	 */
	public static final String CATEGORY_TO_PROPERTY_KEY = "categoryToPropertyKey";

	/**
	 * Key for attribute containing category mapped to its parent in the
	 * terminology configuration.
	 */
	public static final String CATEGORY_TO_PARENT = "categoryToParent";

	/**
	 * Key for attribute containing category mapped to its attributes (user interface labels)
	 * terminology configuration.
	 */
	public static final String CATEGORY_TO_ATTRIBUTES = "categoryToAttributes";

	/** Key for attribute containing set of registrables in given terminology. */
	public static final String REGISTRABLE_CATEGORIES = "registerableCategories";

	/** Key for attribute containing set of submittables in given terminology. */
	public static final String SUBMITTABLE_CATEGORIES = "submittableCategories";

	/**
	 * Key for attribute containing role key in terminology configuration mapped
	 * to its role in the current context.
	 */
	public static final String ROLE_KEY_TO_ROLE = "roleKeyToRoles";
}
