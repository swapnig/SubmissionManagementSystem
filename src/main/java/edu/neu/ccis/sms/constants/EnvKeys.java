package edu.neu.ccis.sms.constants;

/**
 * Interface containing keys for all the environment variables that can be used to interact with the application
 *
 * @author Swapnil Gupta
 * @version SMS 1.0
 * @since May 14, 2015
 */
public interface EnvKeys {

	/**
	 * Key for the main configuration xml file for an individual application
	 * instance from environment variable
	 */
	public static final String CONFIG_XML_PATH_ENV_VAR = "configXMLPath";

	/**
	 * Key for the flag indicating that the main configuration file, contains
	 * additional configuration files from environment variable.
	 */
	public static final String MULTI_CONFIG_FILES_ENV_VAR = "multipleConfigFiles";
}
