package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.ErrorMessageKeys;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.CategoryDao;
import edu.neu.ccis.sms.dao.categories.CategoryDaoImpl;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.entity.categories.Category;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberAttribute;

/**
 * Create a new member based on the category attributes specified in the terminology xml
 * and the values provided by the user for these attributes.
 * Member name should be unique for its direct parent member.
 * 
 * @author Swapnil Gupta
 * @createdOn May 25, 2015
 *
 */
@WebServlet("/CreateMember")
public class CreateMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CreateMemberServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * On receiving the request creates a new member, if a member with the same name as the new member
     * does not already exists for its direct parent member.
     * 
     * If it exists show an error message to the user indicating the requirement of unique member name
     * for a given parent member.
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * On the front-end it is ensured that the sequence of the member attribute fields is same as sequence
         * of category attributes in the terminology xml. Here we get all the request parameters as a list,
         * this ensures that the sequence of the member attributes would be maintained.
         * 
         * This is important as we store the value of the first member attribute as the name of member as well.
         * Which in this case becomes the first request parameter, this is ensured from the front-end
         * 
         * To maintain this sequence in the database as well we use a linked hash set for storing member attributes.
         * This is particular important then retrieving stored member attributes, where we want to record that the
         * first member attribute was indeed the name of the member
         */
        ArrayList<String> paramNames = Collections.list(request.getParameterNames());
        String memberNameParam = paramNames.get(0);
        String memberName = request.getParameter(memberNameParam);
        Set<MemberAttribute> memberAttributes = new LinkedHashSet<MemberAttribute>();

        String parentMemberId = request.getParameter(RequestKeys.PARAM_PARENT_MEMBER_ID);
        MemberDao memberDao = new MemberDaoImpl();

        // Get the userId from session, of the user who is trying to create this member.
        Long userId = (Long) request.getSession(false).getAttribute(SessionKeys.keyUserId);

        // Response data from the server
        StringBuffer responseData = new StringBuffer();

        /*
         * Check whether the parent member for the new member already has a direct child with the same name.
         * If such a member exist display an error message, else create the new member
         */
        if (memberDao.doesMemberNameExistForParentMember(memberName, Long.parseLong(parentMemberId))) {
            responseData.append("<font size='4' color='red'>" + ErrorMessageKeys.MEMBER_EXISTS_FOR_PARENT +"</font>");
            LOGGER.info("User id: " + userId + " attempted to create new member with existing unique Id: "
                    + memberName);
        } else {
            Member newMember = new Member();
            newMember.setName(memberName);

            /*
             * Below we get the category and parentMemberId from request parameters and remove them from the
             * request parameter list. This is needed as later we iterate over all the remaining
             * parameters considering them as member attributes.
             * This gives us the flexibility to have any number of attributes for the member
             */

            // We get the category by name, which is supposed to unique across all the application instances.
            CategoryDao categoryDao = new CategoryDaoImpl();
            Category category = categoryDao.getCategoryByName(request.getParameter(RequestKeys.PARAM_CATEGORY_NAME));
            newMember.setCategory(category);
            paramNames.remove(RequestKeys.PARAM_CATEGORY_NAME);


            // We get the parent member by their id
            MemberDao parentMemberDao = new MemberDaoImpl();
            Member parentMember = parentMemberDao.getMember(Long.parseLong(parentMemberId));
            newMember.setParentMember(parentMember);
            paramNames.remove(RequestKeys.PARAM_PARENT_MEMBER_ID);

            // Iterate over all the remaining request parameters, storing each as member attributes in a linked hash set
            for (String paramName : paramNames) {
                String paramValue = request.getParameter(paramName);
                memberAttributes.add(new MemberAttribute(paramName, paramValue, newMember));
            }

            //Set the member attributes and save the member in back-end
            newMember.setAttributes(memberAttributes);
            memberDao.saveMember(newMember, userId);

            responseData.append(memberName + " has been created successfully");
            LOGGER.info("User id: " + userId + " created new member " + memberName);
        }

        response.setContentType("text/html");
        response.getWriter().write(responseData.toString());
    }
}
