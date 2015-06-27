/**
 * 
 */
package edu.neu.ccis.sms.constants;

/**
 * Interface containing keys for the regex used in restricting field values
 *
 * @author Swapnil Gupta
 * @date 10-June-2015
 * @lastUpdate 10-June-2015
 */
public interface RegexPattern {

    /**
     * Key for regex that only allows alphanumeric characters, underscore, hyphen and space;
     * Currently used to restrict values for the first attribute for a member, which is used as
     * member name.
     */
    public static final String ALPHANUM_UNDERSCORE_HYPHEN_SPACE = "^[a-zA-Z0-9_- ]*$";
}
