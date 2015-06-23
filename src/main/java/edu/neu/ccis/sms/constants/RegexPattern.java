/**
 * 
 */
package edu.neu.ccis.sms.constants;

/**
 * Interface containing keys for the regex used in restricting field values
 *
 * @author Swapnil Gupta
 * @version SMS 1.0
 * @since June 19, 2015
 */
public interface RegexPattern {

    /**
     * Key for regex that only allows alphanumeric characters, underscore and hyphen
     * Currently used to restrci the forst attribute for a member
     */
    public static final String ALPHANUM_UNDERSCORE_HYPHEN_SPACE = "^[a-zA-Z0-9_- ]*$";
}
