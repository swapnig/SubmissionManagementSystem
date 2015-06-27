package edu.neu.ccis.sms.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.configuration.CompositeConfiguration;

/**
 * Utility class for debugging current configuration; list all configuration
 * properties, including properties that have multiple values.
 * 
 * DEBUG PURPOSE ONLY - No current references.
 * 
 * @author Swapnil Gupta
 * @date 18-May-2015
 * @lastUpdate 10-June-2015
 */
public class DebugConfiguration {

    /** Logger for ConfigurationLoader class */
    private static final Logger LOGGER = Logger.getLogger(DebugConfiguration.class.getName());

    /**
     * Print all the properties in the current configuration, including properties that
     * have multiple values associated with them
     * @param config
     *          configuration containing the terminology for current application context
     */
    public static void printKeyValue(final CompositeConfiguration config) {
        Iterator<String> keys = config.getKeys();
        LOGGER.info("Listing all configuration properties");
        while (keys.hasNext()) {
            String key = keys.next();
            StringBuffer configurationProperty = new StringBuffer();
            configurationProperty.append(key + ":");

            Object property = config.getProperty(key);
            if(property instanceof Collection) {
                List<Object> properties = config.getList(key);
                for (Object propertyKey : properties) {
                    configurationProperty.append(propertyKey + ",");
                }
            } else {
                configurationProperty.append(property);
            }
            LOGGER.info(configurationProperty.toString());
        }
    }
}
