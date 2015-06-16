package edu.neu.ccis.sms.servlets.members.read;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
 * Read all the child members for the given category and parent member id,
 * For the root category members, get the members just based on the root category.
 * 
 * @author Swapnil Gupta
 * @since May 27, 2015
 * @version SMS 1.0
 *
 */
@WebServlet("/ReadMembers")
public class ReadMembersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReadMembersServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		StringBuffer content = new StringBuffer();

		String parentMemberId = request.getParameter(RequestKeys.PARAM_PARENT_MEMBER_ID);
		String categoryName = request.getParameter(RequestKeys.PARAM_CATEGORY_NAME);

		CategoryDao categoryDao = new CategoryDaoImpl();
		Category category = categoryDao.getCategoryByName(categoryName);
		Set<Member> membersSet = new HashSet<Member>();

		// For root category we use category as member selector, i.e. get all members for a given category
		// For all other categories we use the category_id and parent_member_id to get its valid members
		if(StringUtils.isEmpty(parentMemberId)) {
			membersSet = category.getMembers();
		} else {
			MemberDao memberDao = new MemberDaoImpl();
			Long categoryId = category.getId();
			membersSet = memberDao.findAllMembersByCategoryAndParentMember(categoryId, Long.parseLong(parentMemberId));
		}

		ArrayList<Member> members = new ArrayList<Member> (membersSet);
		Collections.sort(members, new MemberComparator());

		content.append("<option value='default'></option>");
		for (Member member : members) {
			content.append("<option value=" + member.getId() + ">" + member.getName() + "</option>");
		}

		response.setContentType("text/html");
		response.getWriter().write(content.toString());
	}

}
