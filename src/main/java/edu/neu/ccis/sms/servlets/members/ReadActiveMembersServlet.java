package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import edu.neu.ccis.sms.comparator.MemberComparator;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.dao.categories.CategoryDao;
import edu.neu.ccis.sms.dao.categories.CategoryDaoImpl;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.entity.categories.Category;
import edu.neu.ccis.sms.entity.categories.Member;

/**
 * Read all the active child members for the given category and parent member id,
 * presenting these members formatted as values of a select box.
 * 
 * <br><br>This servlet expects following request parameters -
 * <br>1) {@link edu.neu.ccis.sms.constants.RequestKeys#PARAM_PARENT_MEMBER_ID}
 * - Member for which existing active child members needs to be fetched
 * <br>2) {@link edu.neu.ccis.sms.constants.RequestKeys#PARAM_CATEGORY_NAME}
 * - Category for which existing active members needs to be fetched
 * 
 * @author Swapnil Gupta
 * @date 16-June-2015
 * @lastUpdate 20-June-2015
 *
 */
@WebServlet("/ReadActiveMembers")
public class ReadActiveMembersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ReadActiveMembersServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReadActiveMembersServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * When the request is received, it provides all the active child members for given category,
     * with their parent as the given parent member id.
     * 
     * The child members are formatted as option values of a select box. First value is a default option.
     * If no such child member exist, only default option is returned
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Get the parent member id and category from request parameters, both these are used to find
         * all the child members of given category with their parent as given parent member
         */
        String parentMemberId = request.getParameter(RequestKeys.PARAM_PARENT_MEMBER_ID);
        String categoryName = request.getParameter(RequestKeys.PARAM_CATEGORY_NAME);

        /*
         * We get the category by name, which is supposed to unique across all the application instances.
         */
        CategoryDao categoryDao = new CategoryDaoImpl();
        Category category = categoryDao.getCategoryByName(categoryName);
        Set<Member> membersSet = new HashSet<Member>();

        /*
         * Root category does not have any parent member so we only use category as member selector,
         * i.e. get all members for a given category
         * 
         * For all other categories we use the category_id and parent_member_id to get its valid members
         */
        if(StringUtils.isEmpty(parentMemberId)) {
            membersSet = category.getMembers();
        } else {
            MemberDao memberDao = new MemberDaoImpl();
            Long categoryId = category.getId();

            // Get all the active child members of given category with their parent as given parent member
            membersSet =
                    memberDao.findActiveMembersByCategoryAndParentMember(categoryId, Long.parseLong(parentMemberId));
        }

        LOGGER.info("Fetching all active child members for parent member id: " + parentMemberId + " with category "
                + categoryName);

        /*
         * Sort all the child members by the alphabetic order of their name, using a custom comparator.
         * This is done so that all the child members are not presented in a random order, since the back-end
         * implementation stores members as a hash set
         */
        ArrayList<Member> members = new ArrayList<Member> (membersSet);
        Collections.sort(members, new MemberComparator());

        // Response data from the server
        StringBuffer responseData = new StringBuffer();

        // Build option list for the member select box, one member at a time as an option element for selectbox
        responseData.append("<option value='default'></option>");
        for (Member member : members) {
            responseData.append("<option value=" + member.getId() + ">" + member.getName() + "</option>");
        }

        response.setContentType("text/html");
        response.getWriter().write(responseData.toString());
    }

}
