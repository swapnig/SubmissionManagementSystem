package edu.neu.ccis.sms.servlets.members.update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.entity.categories.MemberAttribute;
import edu.neu.ccis.sms.entity.categories.Member;

import edu.neu.ccis.sms.util.PrintInfo;

/**
 * Servlet implementation class SaveMemberServlet
 * @author Swapnil Gupta
 * @date Jun 11, 2015
 * @lastUpdate Jun 11, 2015
 *
 */
@WebServlet("/UpdateMember")
public class UpdateMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintInfo.printRequestParameters(request);
        ArrayList<String> paramNames = Collections.list(request.getParameterNames());
		
		MemberDao memberDao = new MemberDaoImpl();
		Long memberId = Long.parseLong(request.getParameter(RequestKeys.PARAM_MEMBER_ID));
		Member existingMember = memberDao.getMember(memberId);
		
		if (paramNames.contains(RequestKeys.PARAM_MEMBER_NAME)) {
			existingMember.setName(request.getParameter(RequestKeys.PARAM_MEMBER_NAME));
		}
		
		Set<MemberAttribute> memberAttributes = existingMember.getAttributes();
		for (MemberAttribute memberAttribute : memberAttributes) {
			String memberName = memberAttribute.getName();
			if (paramNames.contains(memberName)) {
				memberAttribute.setValue(request.getParameter(memberName));
			}
		}
		existingMember.setAttributes(memberAttributes);
		
		memberDao.updateMember(existingMember);
		
		StringBuffer content = new StringBuffer();
		content.append("Data updated into database");
		response.setContentType("text/html");
		response.getWriter().write(content.toString());
	}
}
