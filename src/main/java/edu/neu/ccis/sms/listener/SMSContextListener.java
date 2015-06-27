package edu.neu.ccis.sms.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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

/**
 * Setup servlet context for an individual application instance, includes extracting application terminology from
 * the configuration files and storing it in the program memory. It also initializes Alfresco and Hiberante.
 * 
 * <br><br>
 * This is done only once during the application lifecycle during the application initialization.
 * Here all the configurations are read from the application terminology and are stored in application(heap) memory.
 * This ensures that the configuration files are read only once and all the server requests, get the required
 * values from the main memory.
 * <br><br>Details of all the attributes stored in the context are provided with method :
 * setupTerminologyConfigurationInContext.
 * 
 * @author Swapnil Gupta
 * @date 17-May-2015
 * @lastUpdate 22-June-2015
 */
@WebListener
public class SMSContextListener implements ServletContextListener {

    /** Logger for current file */
    private static final Logger LOGGER = Logger.getLogger(SMSContextListener.class.getName());

    /** The terminology config. */
    private CompositeConfiguration terminologyConfig = null;

    /** The category to property key. */
    private LinkedHashMap<String, String> categoryToPropertyKey = null;

    /** The category to its attributes. */
    private HashMap<String, ArrayList<String>> categoryToAttributes = null;

    /** The category to parent. */
    private HashMap<String, String> categoryToParent = null;

    /** The registerable categories. */
    private HashSet<String> registerableCategories = null;

    /** The submittable categories. */
    private HashSet<String> submittableCategories = null;

    /** The role key to roles. */
    private HashMap<String, String> roleKeyToRoles = null;

    /**
     * Loads terminology from configuration sources and reads data into data structures for future access. List of data
     * structures is documented in @see setupTerminologyConfigurationInContext
     */
    public SMSContextListener() {
        try {
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
        } catch (NoSuchElementException exception) {
            // This indicates that one of the attributes expected in the terminology configuration was not found
            LOGGER.info("One of the attributes expected in the terminology configuration was not found."
                    + "Look for all the required attributes in edu.neu.ccis.sms.constants.ConfigKeys.java");
            exception.printStackTrace();
            System.exit(1);
        }
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
        CMISConnector.getCMISSession(config);

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
     * <br><br>1. {@link edu.neu.ccis.sms.constants.ContextKeys#TERMINOLOGY_CONFIG}) : configuration containing the terminology
     * for current application context.
     * 
     * <br>2. {@link edu.neu.ccis.sms.constants.ContextKeys#CATEGORY_TO_PROPERTY_KEY}): category mapped to its property key
     * in the terminology configuration.
     * 
     * <br>3. {@link edu.neu.ccis.sms.constants.ContextKeys#CATEGORY_TO_ATTRIBUTES}): category mapped to its attributes in
     * the terminology configuration.
     * 
     * <br>4. {@link edu.neu.ccis.sms.constants.ContextKeys#CATEGORY_TO_PARENT}) : category mapped to its parent in the
     * terminology configuration.
     * 
     * <br>5. {@link edu.neu.ccis.sms.constants.ContextKeys#CATEGORY_TO_PARENT}) : set of registrables in given terminology.
     * 
     * <br>6. {@link edu.neu.ccis.sms.constants.ContextKeys#CATEGORY_TO_PARENT}) : set of submittables in given terminology.
     * 
     * <br>7. {@link edu.neu.ccis.sms.constants.ContextKeys#ROLE_KEY_TO_ROLE}) : generic roles in terminology configuration
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
     * Inserts all the categories in the current terminology, if they do not already exist in database. If existing
     * categories are encountered they are ignored. Categories need to be unique across all terminologies, although they
     * can be same for different instances of same terminology configuration.
     */
    private void insertNewCategoriesFromTerminology() {
        CategoryDao categoryDao = new CategoryDaoImpl();

        for (Map.Entry<String, String> entry : categoryToPropertyKey.entrySet()) {
            String categoryName = entry.getKey();
            if (null == categoryDao.getCategoryByName(categoryName)) {
                LOGGER.info("Creating category: " + categoryName);
                String parentCategoryName = categoryToParent.get(categoryName);

                Category category = new Category();
                category.setName(categoryName);
                category.setParentCategory(categoryDao.getCategoryByName(parentCategoryName));
                category.setRegisterable(registerableCategories.contains(categoryName));
                category.setSubmittable(submittableCategories.contains(categoryName));
                categoryDao.saveCategory(category);
            } else {
                LOGGER.info("Category already exist, cannot create: " + categoryName);
            }
        }
    }

    /**
     * Inserts the member for root category, this can be done only once during the system startup. Member name needs to be
     * unique for the given root category. On system restart or if the name corresponding to an existing member is used
     * it will ignored and this will be logged in the logs. The name of this member is specified the configuration xml
     * using the attribute. {@link edu.neu.ccis.sms.constants.ConfigKeys#HIERARCHY_ROOT_MEMBER_ELEMENT}.
     * 
     * <br><br>If the root member is being newly created, it is assigned a conductor with the username specified in the
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
            LOGGER.info("Member " + rootMemberName + " for root category already exist, cannot create it again.");
        } else {
            Member rootMember = new Member();
            rootMember.setCategory(rootCategory);
            rootMember.setName(rootMemberName);

            memberDao.saveMember(rootMember);
            LOGGER.info("Member " + rootMemberName + " for root category does not exist, created it now.");
            createConductorForMember(rootCategoryPropertyKey, rootMember);
        }
    }

    /**
     * Creates the user specified in the configuration files as the condcutor for this new root member, if the user not
     * already the conductor for this member
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
            LOGGER.info(rootUser.getUsername() + " does not exist creating it now");

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
            LOGGER.info("Root member: " + rootMember.getName() + " already has " + rootUser.getUsername()
                    + " as its conductor");
        } else {
            UserToMemberMapping mapping = new UserToMemberMapping();
            mapping.setMember(rootMember);
            mapping.setRole(RoleType.CONDUCTOR);
            mapping.setUser(rootUser);
            mappingDao.saveUserToMemberMapping(mapping);
            LOGGER.info("Root member: " + rootMember.getName() + " does not have " + rootUser.getUsername()
                    + " as its conductor. It has been now made its conductor");
        }
    }
}
