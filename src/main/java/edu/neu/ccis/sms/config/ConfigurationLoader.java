package edu.neu.ccis.sms.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import edu.neu.ccis.sms.constants.EnvKeys;

/**
 * Load customized terminology for an individual web application instance as CompositeConfiguration,
 * in form of properties from configuration sources.
 * 
 * <br><br>Expects the path of root configuration xml in an
 * environment variable {@link edu.neu.ccis.sms.constants.EnvKeys#CONFIG_ROOT_XML_PATH_ENV_VAR}.
 * 
 * 
 * <br><br>Composite configuration is used so that configuration sources of different types such as
 * XMLConfiguration, Properties configuration can be used.
 * 
 * 
 * <br>Currently supported configurations are XML and PropertiesConfiguration, which are the most
 * frequently used for specifying configuration properties.
 * Other configurations can be used as well by adding similar file identifiers.
 * 
 * 
 * <br><br>Root configuration xml file is the file which contains path of the actual configuration files.
 * This avoids cluttering of actual configuration files with names of other configuration files.
 * In root configuration xml different files types are distinguished using different file identifiers
 * as described above.
 * 
 * <br><br>Configuration files can have any structure, as long as they have all the attributes and elements specified in
 * {@link edu.neu.ccis.sms.constants.ConfigKeys} This is required since these attributes are used to extract
 * application specific terminology from configuration files.
 * 
 * <br>Note : For XML configuration the topmost root element is ignored, so make the element you want as the root,
 *            child member of a pseudo root element.
 *
 * @author Swapnil Gupta
 * @date 11-May-2015
 * @lastUpdate 13-June-2015
 */
public class ConfigurationLoader {

    /** Logger for ConfigurationLoader class */
    private static final Logger LOGGER = Logger.getLogger(ConfigurationLoader.class.getName());

    /** Configuration source type identifier for xml files */
    private static final String FILE_IDENTIFIER_XML = "dom4j[@fileName]";

    /** Configuration source type identifier for property files */
    private static final String FILE_IDENTIFIER_PROPERTIES = "properties[@fileName]";

    /**
     * Load application terminology from all the configuration files(s) for an
     * individual application instance in a CompositeConfiguration.
     * 
     * <br><br>If any of the configuration file is not found halt program execution. This is
     * critical since the application terminology within the configuration files is used to dynamically
     * build an application specific user interface. Any missing terminology may lead to an inconsistent UI.
     *
     * @return composite configuration for an individual application instance
     */
    public CompositeConfiguration loadConfiguration() {
        CompositeConfiguration terminologyConfig = null;
        try {
            /*
             * Load the root xml using XML configuration and add it to the CompositeConfiguration
             * for the application
             */
            terminologyConfig = new CompositeConfiguration();
            String envConfigXmlPath = getConfigXMLPathFromEnvironemnt();
            terminologyConfig.addConfiguration(new XMLConfiguration(envConfigXmlPath));

            /*
             * Iteratively add all the configuration sources specified in the root xml.
             * 
             * PrintInfo.listAllConfigurationProperties(terminologyConfig) :
             * Can be used to list all the properties currently in the configuration sources
             */
            addAllConfigurationSources(terminologyConfig);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return terminologyConfig;
    }

    /**
     * Iteratively add configurations from all the configurations sources specified in the
     * root configuration file.
     *
     * @param terminologyConfig CompositeConfiguration
     *            object containing all the configuration properties
     * @throws ConfigurationException
     *             the configuration exception
     */
    private void addAllConfigurationSources(final CompositeConfiguration terminologyConfig)
            throws ConfigurationException {

        /*
         * Get an iterator for all the properties currently in the configuration.
         * As only root configuration file is yet added to the configuration, each of the current property
         * corresponds to a configuration source type and it value corresponds to a configuration source.
         */
        Iterator<String> configurationSourceTypes = terminologyConfig.getKeys();
        while (configurationSourceTypes.hasNext()) {
            String configurationSourceType = configurationSourceTypes.next();

            /*
             * Get an individual property from configuration, which corresponds to a configuration source type
             * We check whether multiple configuration sources for a single configuration source type exist,
             * If they exist, retrieve them as a list of configuration sources.
             * 
             * For each configuration source add it to the terminology configuration for the entire application
             */
            Object configSource = terminologyConfig.getProperty(configurationSourceType);
            if(configSource instanceof Collection) {
                List<Object> configSources = terminologyConfig.getList(configurationSourceType);
                for (Object aConfigSource : configSources) {
                    addConfigurationSource(configurationSourceType, (String) aConfigSource, terminologyConfig);
                }
            } else {
                addConfigurationSource(configurationSourceType, (String)configSource, terminologyConfig);
            }
        }
    }

    /**
     * Add all the configuration properties from a single configuration file, based on the file identifier of the
     * configuration file. Currently supports XML and properties configuration files, which are the most prevalent
     * file types used to provide configuration.
     * <br> <br>New file types can be specified by adding separate file identifiers for new file types.
     * 
     * @param configurationSourceType String
     *              Identifier for configuration source type, which is used to identify the type of configuration
     *              to load the given configuration source
     * @param configSource String
     *              Configuration source to be added to the terminology configuration for the entire application
     * @param terminologyConfig CompositeConfiguration
     *              terminology configuration for the entire application
     * @throws ConfigurationException
     */
    private static void addConfigurationSource(final String configurationSourceType, final String configSource,
            final CompositeConfiguration terminologyConfig) throws ConfigurationException {
        switch (configurationSourceType) {
            case FILE_IDENTIFIER_XML:
                LOGGER.info("Adding configuration source: " + configSource + " as XML configuration");
                terminologyConfig.addConfiguration(new XMLConfiguration(configSource));
                break;
            case FILE_IDENTIFIER_PROPERTIES:
                LOGGER.info("Adding configuration source: " + configSource + " as Properties configuration");
                terminologyConfig.addConfiguration(new PropertiesConfiguration(configSource));
                break;
        }
    }

    /**
     * Get the path of the root configuration xml file for an individual application
     * instance, from environment variable
     * {@link edu.neu.ccis.sms.constants.EnvKeys#CONFIG_ROOT_XML_PATH_ENV_VAR}
     *
     * @return the configuration xml path for individual application instance
     */
    private String getConfigXMLPathFromEnvironemnt() {
        String configXmlPath = System.getProperty(EnvKeys.CONFIG_ROOT_XML_PATH_ENV_VAR);
        LOGGER.info("Using configuration from file: " + configXmlPath);
        return configXmlPath;
    }
}
