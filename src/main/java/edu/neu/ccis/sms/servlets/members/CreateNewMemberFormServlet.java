package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.ContextKeys;
import edu.neu.ccis.sms.constants.ErrorMessageKeys;
import edu.neu.ccis.sms.constants.RegexPattern;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.entity.users.RoleType;

/**
 * Create a new member html form from the application terminology, based on its category and parent member,
 * providing user with the controls to submit this form and create a new member.
 * 
 * @author Swapnil Gupta
 * @createdOn May 28, 2015
 *
 */
@WebServlet("/CreateNewMemberForm")
public class CreateNewMemberFormServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CreateNewMemberFormServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateNewMemberFormServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Upon receiving the request respond with new member form for the given category and parent member,
     * If the user has conductor access on the given parent member,
     * Else respond with an authorized member creation error message.
     * 
     * Currently all the category attributes are considered as labels for input type text fields.
     * First field is a text field, all the subsequent fields are text area
     * If other input fields are to be considered they need to be configured in the terminology,
     * identified below and used to build the html form appropriately
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Get the parent member id from request parameters, this parent member id is passed as a
         * hidden attribute in the new member form, to track the parent member for this new member
         */
        Long parentMemberId = Long.parseLong(request.getParameter(RequestKeys.PARAM_PARENT_MEMBER_ID));

        /*
         * Get the category for which member is being created, from request parameters, the category name
         * is passed as a hidden attribute in the new member form, to track the category for this new member
         */
        String categoryName = request.getParameter(RequestKeys.PARAM_CATEGORY_NAME);

        // Get the userId from session, of the user who is trying to create this member.
        UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();
        Long userId = (Long) request.getSession(false).getAttribute(SessionKeys.keyUserId);

        // Response data from the server
        StringBuffer responseData = new StringBuffer();

        /*
         * As only conductor should be allowed to create a new child member for a given member.
         * Check whether the current user has conductor access for the parent member.
         * If the user has conductor access on parent member allow child member creation,
         * else show an error message indicating attempt of unauthorized member creation
         */
        if (userToMemberMappingDao.doesUserHaveRoleForMember(userId, RoleType.CONDUCTOR, parentMemberId)) {
            LOGGER.info("UserId: " + userId + " requested add member form for category " + categoryName +
                    " and parent member id: " + parentMemberId);

            /*
             * Get the category attributes for new member, equivalent to html labels for new member form.
             * These labels are stored in servlet context as a HashMap with key as category, and value as
             * list of attributes
             */
            ServletContext context = getServletContext();
            @SuppressWarnings("unchecked")
            HashMap<String, ArrayList<String>> categoryToHtmlLabels =
            (HashMap<String, ArrayList<String>>) context.getAttribute(ContextKeys.CATEGORY_TO_ATTRIBUTES);
            ArrayList<String> htmlLabels = categoryToHtmlLabels.get(categoryName);

            int index = 0;
            for(String htmlLabel : htmlLabels) {
                /*
                 * Each category attribute is used as label for the new member form.
                 * 
                 * Text form capitalize is used here to have a consistent appearance of member attributes.
                 * Thus ignoring the letter case specified in the terminology xml.
                 * 
                 * First field is a text field
                 * All the subsequent fields are text area
                 * 
                 * Special restrictions on the first field as its value will be used as the member name.
                 * 1. Restriction on the characters that can be used in forming the value of this attribute.
                 * 2. Specify it as a required field for form submission
                 */
                if(index++ == 0) {
                    responseData.append("<tr><td>"
                            + "<label style='text-transform: capitalize'>" + htmlLabels.get(0) + "*</label>:"
                            + "</td>");

                    responseData.append("<td>"
                            + "<input type='text' name='" + htmlLabels.get(0)
                            + "' pattern='" + RegexPattern.ALPHANUM_UNDERSCORE_HYPHEN_SPACE
                            + "' required> (only alphanumeric, underscore, hyphen)"
                            + "</td></tr>");
                } else {
                    responseData.append("<tr><td>"
                            + "<label style='text-transform: capitalize'>" + htmlLabel
                            + "</label>:</td>");

                    responseData.append("<td>"
                            + "<textarea name='" + htmlLabel + "'></textarea>"
                            + "</td></tr>");
                }
            }

            /*
             * Add a form submission button for creating a new member and hidden fields to track the category and
             * parent member for which the new member is being created.
             */
            responseData.append("<tr colspan='2'><td><input type='submit' id='createMember' value='Create "
                    + categoryName + "'></td></tr>");
            responseData.append("<input type='hidden' name='category' value='" + categoryName
                    + "' style='display:none'>");
            responseData.append("<input type='hidden' id ='parentMemberId' name='parentMemberId' value='"
                    + parentMemberId + "' style='display:none'>");

        } else {
            LOGGER.info("Unauthorized user with UserId: " + userId + " requested new member form for category " + categoryName +
                    " and parent member id: " + parentMemberId);
            responseData.append("<font size='4' color='red'>" + ErrorMessageKeys.UNAUTHORIZED_MEMBER_CREATION + "</font>");
        }

        response.setContentType("text/html");
        response.getWriter().write(responseData.toString());
    }
}
