package edu.neu.ccis.sms.comparator;

import java.util.Comparator;

import edu.neu.ccis.sms.entity.categories.Member;

/**
 * A comparison function, which imposes a total ordering on Member based on its
 * name property.
 *
 * @author Swapnil Gupta
 * @version SMS 1.0
 * @since Jun 13, 2015
 */
public class MemberComparator implements Comparator<Member> {

    /**
     * Compares two Member based on their name property.
     *
     * @param m1
     *            the first member to be compared.
     * @param m2
     *            the second member to be compared.
     * @return the int a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the
     *         second.
     */
    @Override
    public int compare(final Member m1, final Member m2) {
        return m1.getName().compareTo(m2.getName());
    }
}
