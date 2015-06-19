package edu.neu.ccis.sms.config;

import java.util.Iterator;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import edu.neu.ccis.sms.constants.EnvKeys;

/**
 * Load customized terminology for an individual web application instance as
 * CompositeConfiguration, in form of properties from xml configuration sources.
 * 
 * Can read properties from multiple XML configuration files.
 * 
 * Expects the path of configuration xml in an environment variable
 * {@link edu.neu.ccis.sms.constants.EnvKeys#CONFIG_XML_PATH_ENV_VAR}.
 * 
 * Usage of multiple configuration files can be indicated by environment
 * variable {@link edu.neu.ccis.sms.constants.EnvKeys#CONFIG_XML_PATH_ENV_VAR}.
 *
 * @author Swapnil Gupta
 * @version SMS 1.0
 * @since May 11, 2015
 */
public class ConfigurationLoader {

    /**
     * Load application terminology from all the configuration files(s) for an
     * individual application instance in CompositeConfiguration. Currently
     * supports XML configuration files only, because of better support for
     * customizations in form of multi-level hierarchies.
     *
     * @return composite configuration for an individual application instance
     */
    public CompositeConfiguration loadConfiguration() {
        CompositeConfiguration terminologyConfig = null;
        try {
            /**
             * Read the customization terminology from the root configuration
             * XML. Other configurations can be used as well but have need be
             * used because they do not support multi-level hierarchies well. If
             * used they need to be loaded appropriately through associated
             * interface like PropertyConfiguration
             */
            terminologyConfig = new CompositeConfiguration();
            String envConfigXmlPath = getConfigXMLPathFromEnvironemnt();
            terminologyConfig.addConfiguration(new XMLConfiguration(
                    envConfigXmlPath));

            /**
             * If the multiple configuration file status has some value,
             * iteratively load configurations from all the configuration files
             */
            if (null != getMultipleConfigFileStatus()) {
                addConfigurationsFromSources(terminologyConfig);
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return terminologyConfig;
    }

    /**
     * Add configurations from all the configurations sources specified in the
     * root configuration file.
     *
     * @param terminologyConfig
     *            CompositeConfiguration object containing all the configuration
     *            properties
     * @throws ConfigurationException
     *             the configuration exception
     */
    private void addConfigurationsFromSources(
            final CompositeConfiguration terminologyConfig)
                    throws ConfigurationException {

        /**
         * Get all the properties currently in the root configuration xml file,
         * which would be one key per configuration file. Here we assume that
         * all the configuration files are xml and use the xml configuration to
         * load them. If sources other than xml needs to be read then they need
         * to be distinguished below and added appropriately
         */
        Iterator<String> keys = terminologyConfig.getKeys();
        while (keys.hasNext()) {
            String configSource = terminologyConfig.getString(keys.next());
            terminologyConfig.addConfiguration(new XMLConfiguration(
                    configSource));
        }
    }

    /**
     * Get the main configuration xml file for an individual application
     * instance from environment variable
     * {@link edu.neu.ccis.sms.constants.EnvKeys#CONFIG_XML_PATH_ENV_VAR}
     *
     * @return the configuration xml path for individual application instance
     */
    private String getConfigXMLPathFromEnvironemnt() {
        System.out.println(System.getProperty(EnvKeys.CONFIG_XML_PATH_ENV_VAR));
        return System.getProperty(EnvKeys.CONFIG_XML_PATH_ENV_VAR);
    }

    /**
     * Get the environment variable indicating that the main configuration file, contains
     * additional configuration files from environment variable
     * {@link edu.neu.ccis.sms.constants.EnvKeys#MULTI_CONFIG_FILES_ENV_VAR}.
     *
     * @return value for the environment variable indicating usage of multiple
     *         configuration files
     */
    private String getMultipleConfigFileStatus() {
        return System.getProperty(EnvKeys.MULTI_CONFIG_FILES_ENV_VAR);
    }
}
