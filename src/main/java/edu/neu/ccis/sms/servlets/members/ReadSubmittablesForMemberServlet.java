package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;

import edu.neu.ccis.sms.comparator.MemberComparator;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;

/**
 * Read all the child submittables for the given member, represented as an unordered list, sorted by their name.
 * 
 * @author Swapnil Gupta
 * @createdOn Jun 10, 2015
 *
 */
@WebServlet("/ReadSubmittablesForMember")
public class ReadSubmittablesForMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ReadSubmittablesForMemberServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReadSubmittablesForMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * When the request is received retrieves all the submittables for the current member,
     * If none exist then return a help message indicating that no submittable child members have been
     *  created for this member yet
     * Else return all such child members as an unordered list
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Get the parent member id from request parameters, this is used to get all its submittable child members.
         * Sort all the child members by the alphabetic order of their name, using a custom comparator.
         * This is done so that all the child members are not presented in a random order, since the back-end
         * implementation stores members as a hash set
         */
        String memberId = request.getParameter(RequestKeys.PARAM_MEMBER_ID);
        MemberDao memberDao = new MemberDaoImpl();
        ArrayList<Member> childSubmittablesForMember =
                new ArrayList<Member> (memberDao.findAllSubmittableMembersByParentMemberId(Long.parseLong(memberId)));
        Collections.sort(childSubmittablesForMember, new MemberComparator());

        // Response data from the server
        StringBuffer responseData = new StringBuffer();

        /*
         * Check whether any submittables exist for the current member,
         * If none exist then return a help message indicating that no submittable child members have been
         *  created for this member yet
         * Else return all such child members as an unordered list
         */
        if (CollectionUtils.isEmpty(childSubmittablesForMember)) {
            responseData.append("No submittables have been created yet.");
            LOGGER.info("No submittables found for member id:" + memberId);
        } else {

            // Build an unordered list, of all the submittables for a given member
            responseData.append("<ul>");
            for (Member childSubmittable : childSubmittablesForMember) {
                String childSubmittableName = childSubmittable.getName();
                responseData.append("<li><a href='/SMS/ViewSubmittableMember?memberId=" + childSubmittable.getId()
                        + "'>" + childSubmittableName + "</a></li>");
            }
            responseData.append("</ul>");
            LOGGER.info("Listed all the submittables for member: " + memberId);
        }

        response.setContentType("text/html");
        response.getWriter().write(responseData.toString());
    }
}
