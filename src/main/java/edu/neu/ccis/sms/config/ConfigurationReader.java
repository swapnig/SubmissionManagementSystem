package edu.neu.ccis.sms.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.CompositeConfiguration;

import edu.neu.ccis.sms.constants.ConfigKeys;

/**
 * Utility class for retrieving properties from the CompositeConfiguration,
 * containing the entire terminology for the individual web application
 * instance.
 *
 * @author Swapnil Gupta
 * @version SMS 1.0
 * @since May 12, 2015
 */
public class ConfigurationReader {

	/**
	 * Get attribute key for given property.
	 *
	 * @param attribute
	 *            the attribute to be retrieved from given property
	 * @param property
	 *            the property from which the given attribute needs to be
	 *            retrieved
	 * @return key to get given attribute from given property
	 */
	public static String getAttributeKeyForProperty(final String attribute, final String property) {
		return property + "[@" + attribute + "]";
	}

	/**
	 * Get attribute key for given property.
	 *
	 * @param child element
	 *            the child element to be retrieved from given property
	 * @param property
	 *            the property from which the given attribute needs to be
	 *            retrieved
	 * @return key to get given child element from given property
	 */
	public static String getChildElementKeyForProperty(final String childElement, final String property) {
		return property + ConfigKeys.HIERARCHY_ELEMENT_DELIMITER  + childElement;
	}

	/**
	 * Find all the categories in the terminology config, mapping them to their
	 * property key. Starting from root element (
	 * {@link edu.neu.ccis.sms.constants.ConfigKeys#HIERARCHY_ROOT_ELEMENT}),
	 * keep on traversing child elements (
	 * {@link edu.neu.ccis.sms.constants.ConfigKeys#HIERARCHY_ROOT_ELEMENT}[.
	 * {@link edu.neu.ccis.sms.constants.ConfigKeys#HIERARCHY_CHILD_ELEMENT}])
	 * e.g category.subCategory.subCategory till you traverse all valid
	 * properties with the attribute 'name'.
	 *
	 * @param terminologyConfig
	 *            configuration containing the terminology for current
	 *            application context
	 * @return all the categories mapped to their property keys in the
	 *         configuration file
	 */
	public static LinkedHashMap<String, String> getCategoryToPropertyKey(
			final CompositeConfiguration terminologyConfig) {

		LinkedHashMap<String, String> categoryToPropertyKey = new LinkedHashMap<String, String>();
		ArrayList<String> propertyKeysToTraverse = new ArrayList<String>();
		propertyKeysToTraverse.add(ConfigKeys.HIERARCHY_ROOT_ELEMENT);

		/*
		 * Continue adding category to property key till the list of property
		 * keys to traverse become empty, Elements are added to the list in the
		 * called method as they are encountered as children category to a
		 * single category
		 */
		while (CollectionUtils.isNotEmpty(propertyKeysToTraverse)) {
			addAllCategoryToPropertyKeyAtSameLevel(propertyKeysToTraverse, categoryToPropertyKey, terminologyConfig);
		}
		return categoryToPropertyKey;
	}

	/**
	 * Get all the categories at the same level (in terminology hierarchy), i.e
	 * with same property key and append them to the category to property key
	 * map. If more than one categories are available for a given property key,
	 * they are siblings (at same level) and can be distinguished by using the
	 * index e.g (0) after the property key, starting from 0 following their
	 * sequence in the config file
	 *
	 * @param propertyKeys
	 *            list of all the properties yet to be traversed
	 * @param categoryToPropertyKey
	 *            mapping from category to its property key in configuration
	 * @param terminologyConfig
	 *            configuration containing the terminology for current
	 *            application context
	 */
	private static void addAllCategoryToPropertyKeyAtSameLevel(
			final ArrayList<String> propertyKeysToTraverse,
			final LinkedHashMap<String, String> categoryToPropertyKey,
			final CompositeConfiguration terminologyConfig) {

		// Remove a property from list of yet to be traversed properties
		String propertyKey = propertyKeysToTraverse.remove(0);

		String namedAttributeKey = getAttributeKeyForProperty(ConfigKeys.CATEGORY_ATTRIBUTE_NAME, propertyKey);
		Object propertyObj = terminologyConfig.getProperty(namedAttributeKey);

		// If more than one categories at the same level, get all of them as a
		// list and traverse them one by one
		if (propertyObj instanceof Collection) {
			int index = 0;
			List<Object> properties = terminologyConfig.getList(namedAttributeKey);

			/*
			 * Get one category at a time and map it to its indexed (0,1,...)
			 * property key E.g if category.subCategory has 2 sibling categories
			 * they become category.subCategory(0) and category.subCategory(1)
			 * respectively. Add their child category(s) to yet to be traversed
			 * list, which in this case would be
			 * category.subCategory(0).subCategory and
			 * category.subCategory(1).subCategory respectively
			 */
			for (Object property : properties) {
				String indexedPropertyKey = propertyKey + "(" + index++ + ")";
				propertyKeysToTraverse.add(indexedPropertyKey + "." + ConfigKeys.HIERARCHY_CHILD_ELEMENT);
				categoryToPropertyKey.put((String) property, indexedPropertyKey);
			}
			/*
			 * If a single category at one level map it to its property key (e.g
			 * category.subCategory) and Add it is child category
			 * (category.subCategory.subCategory) to yet to be traversed list
			 */
		} else if (null != propertyObj) {
			propertyKeysToTraverse.add(propertyKey + "." + ConfigKeys.HIERARCHY_CHILD_ELEMENT);
			categoryToPropertyKey.put((String) propertyObj, propertyKey);
		}
	}

	/**
	 * Get a map (of all categories in configuration) from category to a list of
	 * its html labels All the html labels should be contained within the '
	 * {@link edu.neu.ccis.sms.constants.ConfigKeys#CATEGORY_ELEMENT_HTML_LABELS}
	 * ' element, child of the '(sub)category' element.
	 *
	 * @param categoryToPropertyKey
	 *            mapping from category to its property key in configuration
	 * @param terminologyConfig
	 *            configuration containing the terminology for current
	 *            application context
	 * @return a map of all the categories mapped to the list of all its html
	 *         labels(member attributes)
	 */
	public static HashMap<String, ArrayList<String>> getCategoryToHtmlLabels(
			final LinkedHashMap<String, String> categoryToPropertyKey,
			final CompositeConfiguration terminologyConfig) {

		HashMap<String, ArrayList<String>> categoryToHtmlLabels = new HashMap<String, ArrayList<String>>();

		Iterator<Entry<String, String>> iterator = categoryToPropertyKey.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> categoryToProperty = iterator.next();
			String category = categoryToProperty.getKey();
			String propertyKey = categoryToProperty.getValue();

			ArrayList<String> htmlLabels = new ArrayList<String>();
			Iterator<String> labelKeys = terminologyConfig.getKeys(propertyKey + "."
					+ ConfigKeys.CATEGORY_ELEMENT_HTML_LABELS);

			// For each html label add it to the labels list for current
			// category
			while (labelKeys.hasNext()) {
				String labelKey = labelKeys.next();
				htmlLabels.add(terminologyConfig.getString(labelKey));
			}
			categoryToHtmlLabels.put(category, htmlLabels);
		}
		return categoryToHtmlLabels;
	}

	/**
	 * Get a map (of all categories in configuration) from category(name) to its
	 * parent category(name). name :
	 * {@link edu.neu.ccis.sms.constants.ConfigKeys#CATEGORY_ATTRIBUTE_NAME}
	 * 
	 * @param categoryToPropertyKey
	 *            , mapping from category to its property key in configuration
	 * @param terminologyConfig
	 *            , configuration containing the terminology for current
	 *            application context
	 * @return a map of all the categories mapped to their parent categories
	 */
	public static HashMap<String, String> getCategoryToParentCategory(
			final LinkedHashMap<String, String> categoryToPropertyKey,
			final CompositeConfiguration terminologyConfig) {

		HashMap<String, String> categoryToParent = new HashMap<String, String>();
		Iterator<Entry<String, String>> iterator = categoryToPropertyKey.entrySet().iterator();

		// Root category is first to be traversed and has no parent, hence its
		// parent is null
		String rootCategory = iterator.next().getKey();
		categoryToParent.put(rootCategory, null);

		while (iterator.hasNext()) {
			Map.Entry<String, String> categoryToProperty = iterator.next();
			String category = categoryToProperty.getKey();
			String propertyKey = categoryToProperty.getValue();

			// For a given category with key category.subCategory.subCategory,
			// the key for its parent is category.subCategory
			String parentPropertyKey = propertyKey.substring(0, propertyKey.lastIndexOf('.'));

			// We want the child to parent mapping using their name, hence get
			// the parent name using the parent property key
			String parentNameAttributeKey = getAttributeKeyForProperty(ConfigKeys.CATEGORY_ATTRIBUTE_NAME,
					parentPropertyKey);

			String parentCategory = terminologyConfig.getString(parentNameAttributeKey);
			categoryToParent.put(category, parentCategory);
		}
		return categoryToParent;
	}

	/**
	 * Return a hash set of all the categories in the terminology which have
	 * true as value for the given boolean attribute. Currently used boolean
	 * attributes
	 * {@link edu.neu.ccis.sms.constants.ConfigKeys#CATEGORY_ATTRIBUTE_REGISTRABLE}
	 * {@link edu.neu.ccis.sms.constants.ConfigKeys#CATEGORY_ATTRIBUTE_SUBMITTABLE}
	 *
	 * @param categoryToPropertyKey
	 *            mapping from category to its property key in configuration
	 * @param terminologyConfig
	 *            configuration containing the terminology for current
	 *            application context
	 * @param booleanAttribute
	 *            boolean attribute we are searching for given category
	 * @return a set of all categories which are <boolean attribute>-able e.g
	 *         for boolean attribute <register> returns a hash set of all
	 *         registrables
	 */
	public static HashSet<String> getAllCategoriesForBooleanAttribute(
			final LinkedHashMap<String, String> categoryToPropertyKey,
			final CompositeConfiguration terminologyConfig,
			final String booleanAttribute) {
		HashSet<String> booleanAttributeCategories = new HashSet<String>();

		for (Map.Entry<String, String> entry : categoryToPropertyKey.entrySet()) {
			String category = entry.getKey();
			String propertyKey = entry.getValue();
			String registerAttributeKey = getAttributeKeyForProperty(booleanAttribute, propertyKey);

			// Get value of registerAttributeKey as a boolean, with default
			// value (for missing property) as false
			if (terminologyConfig.getBoolean(registerAttributeKey, false)) {
				booleanAttributeCategories.add(category);
			}
		}
		return booleanAttributeCategories;
	}

	/**
	 * Given a (parent) property key, get a map (of all) from its child property
	 * and to its value.
	 * 
	 * @param parentProperty
	 *            the property for which we want to get all its
	 *            containing(child) properties
	 * @param terminologyConfig
	 *            configuration containing the terminology for current
	 *            application context
	 * @return a map containing pairs <child property key : value>
	 */
	public static HashMap<String, String> getChildPropertyKeyToValue(final String parentProperty,
			final CompositeConfiguration terminologyConfig) {
		HashMap<String, String> propertyKeyToValues = new HashMap<String, String>();

		Iterator<String> iterator = terminologyConfig.getKeys(parentProperty);
		while (iterator.hasNext()) {
			/*
			 * Get a single qualified child key for given parent property
			 * Qualification is of the form "roles.conductor", where "role" ->
			 * parent property "conductor" -> contained property
			 */
			String propertyKey = iterator.next();

			// Extract the actual property key, removing the parent
			// qualification, (separated by .) i.e get "conductor"
			String propertyName = propertyKey
					.substring(propertyKey.lastIndexOf(ConfigKeys.HIERARCHY_ELEMENT_DELIMITER) + 1);

			String propertyValue = terminologyConfig.getString(propertyKey);
			propertyKeyToValues.put(propertyName, propertyValue);
		}
		return propertyKeyToValues;
	}

}
