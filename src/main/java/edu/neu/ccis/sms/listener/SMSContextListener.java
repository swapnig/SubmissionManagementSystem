package edu.neu.ccis.sms.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.configuration.CompositeConfiguration;

import edu.neu.ccis.sms.config.ConfigurationLoader;
import edu.neu.ccis.sms.config.ConfigurationReader;
import edu.neu.ccis.sms.constants.ConfigKeys;
import edu.neu.ccis.sms.constants.ContextKeys;
import edu.neu.ccis.sms.dao.categories.CategoryDao;
import edu.neu.ccis.sms.dao.categories.CategoryDaoImpl;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.categories.Category;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;
import edu.neu.ccis.sms.util.CMISConnector;
import edu.neu.ccis.sms.util.CMISConnector.CMISConfig;
import edu.neu.ccis.sms.util.HibernateUtil;

// TODO: Auto-generated Javadoc
/**
 * Setup the context for an individual application instance, including configuring Application Terminology, Hibernate
 * and Alfrseco.
 * 
 * Implements ServletContextListener interface, overriding contextInitialized and contextDestroyed events
 * 
 * @author Swapnil Gupta
 * @version SMS 1.0
 * @since May 17, 2015
 */
@WebListener
public class SMSContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(SMSContextListener.class.getName());

    /** The terminology config. */
    private final CompositeConfiguration terminologyConfig;

    /** The category to property key. */
    private final LinkedHashMap<String, String> categoryToPropertyKey;

    /** The category to its attributes. */
    private final HashMap<String, ArrayList<String>> categoryToAttributes;

    /** The category to parent. */
    private final HashMap<String, String> categoryToParent;

    /** The registerable categories. */
    private final HashSet<String> registerableCategories;

    /** The submittable categories. */
    private final HashSet<String> submittableCategories;

    /** The role key to roles. */
    private final HashMap<String, String> roleKeyToRoles;

    /**
     * Loads terminology from configuration sources and reads data into data structures for future access. List of data
     * structures is documented in @see setupTerminologyConfigurationInContext
     */
    public SMSContextListener() {
        terminologyConfig = new ConfigurationLoader().loadConfiguration();
        categoryToPropertyKey = ConfigurationReader.getCategoryToPropertyKey(terminologyConfig);
        categoryToParent = ConfigurationReader.getCategoryToParentCategory(categoryToPropertyKey, terminologyConfig);
        categoryToAttributes = ConfigurationReader.getCategoryToHtmlLabels(categoryToPropertyKey, terminologyConfig);
        registerableCategories = ConfigurationReader.getAllCategoriesForBooleanAttribute(categoryToPropertyKey,
                terminologyConfig, ConfigKeys.CATEGORY_ATTRIBUTE_REGISTRABLE);
        submittableCategories = ConfigurationReader.getAllCategoriesForBooleanAttribute(categoryToPropertyKey,
                terminologyConfig, ConfigKeys.CATEGORY_ATTRIBUTE_SUBMITTABLE);
        roleKeyToRoles = ConfigurationReader.getChildPropertyKeyToValue(ConfigKeys.ROLES_ROOT_ELEMENT,
                terminologyConfig);
    }

    /**
     * Processes the servlet context initialization event, used for setting up application terminology, hibernate and
     * content management system.
     * 
     * @param contextEvent
     *            the context event for current servlet instance
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(final ServletContextEvent contextEvent) {
        LOGGER.info("Method - SMSContextListener:contextInitialized");

        ServletContext context = contextEvent.getServletContext();

        // Setup terminology in application context
        setupTerminologyConfigurationInContext(context);

        // Initialize Hibernate session factory
        HibernateUtil.getSessionFactory();

        // Initialize the CMIS Session
        /* CMIS initialization configs */
        String cmsRepoUsername = context.getInitParameter("CMSUser");
        String cmsRepoPswd = context.getInitParameter("CMSPswd");
        String cmsRepoAtompubBindingUrl = context.getInitParameter("CMSAtomPubBindingURL");
        int cmsRepoNumber = Integer.parseInt(context.getInitParameter("CMSRepoNumber"));

        CMISConfig config = new CMISConfig(cmsRepoUsername, cmsRepoPswd, cmsRepoAtompubBindingUrl, cmsRepoNumber);
        // Initialize the CMIS Session
        CMISConnector.getCMISSession(config);// Initialize Hibernate session factory

        // Insert new categories from terminology if they do not already exist
        insertNewCategoriesFromTerminology();

        // Insert member for root category
        insertRootCategoryMember();
    }

    /**
     * Context destroyed.
     * 
     * @param arg0
     *            the arg0
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    @Override
    public void contextDestroyed(final ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }

    /**
     * Setup terminology configuration in servlet context including few frequently used data structures.
     * 
     * 1. {@link edu.neu.ccis.sms.constants.ContextKeys#TERMINOLOGY_CONFIG}) : configuration containing the terminology
     * for current application context.
     * 
     * 2. {@link edu.neu.ccis.sms.constants.ContextKeys#CATEGORY_TO_PROPERTY_KEY}): category mapped to its property key
     * in the terminology configuration.
     * 
     * 3. {@link edu.neu.ccis.sms.constants.ContextKeys#CATEGORY_TO_ATTRIBUTES}): category mapped to its attributes in
     * the terminology configuration.
     * 
     * 4. {@link edu.neu.ccis.sms.constants.ContextKeys#CATEGORY_TO_PARENT}) : category mapped to its parent in the
     * terminology configuration.
     * 
     * 5. {@link edu.neu.ccis.sms.constants.ContextKeys#CATEGORY_TO_PARENT}) : set of registrables in given terminology.
     * 
     * 6. {@link edu.neu.ccis.sms.constants.ContextKeys#CATEGORY_TO_PARENT}) : set of submittables in given terminology.
     * 
     * 7. {@link edu.neu.ccis.sms.constants.ContextKeys#ROLES_ROOT_ELEMENT}) : role key in terminology configuration
     * mapped to its role in the current context.
     * 
     * @param context
     *            servlet context for current application instance
     */
    private void setupTerminologyConfigurationInContext(final ServletContext context) {
        context.setAttribute(ContextKeys.TERMINOLOGY_CONFIG, terminologyConfig);
        context.setAttribute(ContextKeys.CATEGORY_TO_PROPERTY_KEY, categoryToPropertyKey);
        context.setAttribute(ContextKeys.CATEGORY_TO_ATTRIBUTES, categoryToAttributes);
        context.setAttribute(ContextKeys.CATEGORY_TO_PARENT, categoryToParent);
        context.setAttribute(ContextKeys.REGISTRABLE_CATEGORIES, registerableCategories);
        context.setAttribute(ContextKeys.SUBMITTABLE_CATEGORIES, submittableCategories);
        context.setAttribute(ContextKeys.ROLE_KEY_TO_ROLE, roleKeyToRoles);
    }

    /**
     * Inserts all the categories in the current terminology, if they already do not exist in database. If existing
     * categories are encountered they are ignored. Categories need to be unique across all terminologies, although they
     * can be same for different instances of same terminology configuration.
     */
    private void insertNewCategoriesFromTerminology() {
        CategoryDao categoryDao = new CategoryDaoImpl();

        for (Map.Entry<String, String> entry : categoryToPropertyKey.entrySet()) {
            String categoryName = entry.getKey();
            if (null == categoryDao.getCategoryByName(categoryName)) {
                System.out.println("Creating category: " + categoryName);
                String parentCategoryName = categoryToParent.get(categoryName);

                Category category = new Category();
                category.setName(categoryName);
                category.setParentCategory(categoryDao.getCategoryByName(parentCategoryName));
                category.setRegisterable(registerableCategories.contains(categoryName));
                category.setSubmittable(submittableCategories.contains(categoryName));
                categoryDao.saveCategory(category);
            } else {
                System.out.println("Category already exist, cannot create: " + categoryName);
            }
        }
    }

    /**
     * Inserts the member for root category, this can be only done during the system startup. Member name needs to be
     * unique for the given root category. On system restart or if the name corresponding to an existing member is used
     * it will ignored and this will be logged in the logs. The name of this member is specified the configuration xml
     * using the attribute. {@link edu.neu.ccis.sms.constants.ConfigKeys#HIERARCHY_ROOT_MEMBER_ELEMENT}
     * 
     * If the root member is being newly created, it is assigned a conductor with the username specified in the
     * {@link edu.neu.ccis.sms.constants.ConfigKeys#HIERARCHY_ROOT_USERNAME_ELEMENT}
     * 
     */
    private void insertRootCategoryMember() {

        String rootCategoryName = categoryToPropertyKey.keySet().iterator().next();
        String rootCategoryPropertyKey = categoryToPropertyKey.get(rootCategoryName);
        String rootMemberPropertyKey = ConfigurationReader.getAttributeKeyForProperty(
                ConfigKeys.HIERARCHY_ROOT_MEMBER_ELEMENT, rootCategoryPropertyKey);
        String rootMemberName = terminologyConfig.getString(rootMemberPropertyKey);

        CategoryDao categoryDao = new CategoryDaoImpl();
        Category rootCategory = categoryDao.getCategoryByName(rootCategoryName);

        MemberDao memberDao = new MemberDaoImpl();

        if (memberDao.doesMemberNameExistForParentMember(rootMemberName, null)) {
            System.out
                    .println("Member " + rootMemberName + " for root category already exist, cannot create it again.");
        } else {
            Member rootMember = new Member();
            rootMember.setCategory(rootCategory);
            rootMember.setName(rootMemberName);

            memberDao.saveMember(rootMember);
            System.out.println("Member " + rootMemberName + " for root category does not exist, created it now.");
            createConductorForMember(rootCategoryPropertyKey, rootMember);
        }
    }

    /**
     * Creates the user specified in the configuration files as the condcutor for this new root member, if the user not
     * already the conductor
     * 
     * @param rootCategoryPropertyKey
     *            the property key for root category
     * @param rootMember
     *            the newly created root member which needs to have a conductor
     */
    private void createConductorForMember(final String rootCategoryPropertyKey, final Member rootMember) {

        String usernameKey = ConfigurationReader.getChildElementKeyForProperty(
                ConfigKeys.HIERARCHY_ROOT_USERNAME_ELEMENT, rootCategoryPropertyKey);
        String username = terminologyConfig.getString(usernameKey);

        UserDao userDao = new UserDaoImpl();
        User rootUser = userDao.findUserByUsername(username);

        // Create the root user if it does not already exist
        if (null == rootUser) {
            rootUser = new User();
            rootUser.setUsername(username);
            System.out.println(rootUser.getUsername() + " does not exist creating it now");

            String emailKey = ConfigurationReader.getChildElementKeyForProperty(
                    ConfigKeys.HIERARCHY_ROOT_EMAIL_ELEMENT, rootCategoryPropertyKey);
            rootUser.setEmail(terminologyConfig.getString(emailKey));

            String firstnameKey = ConfigurationReader.getChildElementKeyForProperty(
                    ConfigKeys.HIERARCHY_ROOT_FIRSTNAME_ELEMENT, rootCategoryPropertyKey);
            rootUser.setFirstname(terminologyConfig.getString(firstnameKey));

            String passwordKey = ConfigurationReader.getChildElementKeyForProperty(
                    ConfigKeys.HIERARCHY_ROOT_PASSWORD_ELEMENT, rootCategoryPropertyKey);
            rootUser.setPassword(terminologyConfig.getString(passwordKey));

            userDao.saveUser(rootUser);
        }

        UserToMemberMappingDao mappingDao = new UserToMemberMappingDaoImpl();
        // If the given user is already conductor for root member then do nothing else make user conductor for member
        if (mappingDao.doesUserHaveRoleForMember(rootUser.getId(), RoleType.CONDUCTOR, rootMember.getId())) {
            System.out.println("Root member: " + rootMember.getName() + " already has " + rootUser.getUsername()
                    + " as its conductor");
        } else {
            UserToMemberMapping mapping = new UserToMemberMapping();
            mapping.setMember(rootMember);
            mapping.setRole(RoleType.CONDUCTOR);
            mapping.setUser(rootUser);
            mappingDao.saveUserToMemberMapping(mapping);
            System.out.println("Root member: " + rootMember.getName() + " does not have " + rootUser.getUsername()
                    + " as its conductor. It has been now made its conductor");
        }
    }
}
