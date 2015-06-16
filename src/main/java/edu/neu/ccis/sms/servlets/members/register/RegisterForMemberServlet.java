package edu.neu.ccis.sms.servlets.members.register;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.CompositeConfiguration;

import edu.neu.ccis.sms.config.ConfigurationReader;
import edu.neu.ccis.sms.constants.ConfigKeys;
import edu.neu.ccis.sms.constants.ContextKeys;
import edu.neu.ccis.sms.constants.JspViews;

/**
 * Servlet implementation class RegisterMember
 * @author Swapnil Gupta
 * @date Jun 11, 2015
 * @lastUpdate Jun 11, 2015
 *
 */
@WebServlet("/RegisterForMember")
public class RegisterForMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterForMemberServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		ServletContext context = getServletContext();
		CompositeConfiguration terminologyConfig =
				(CompositeConfiguration) context.getAttribute(ContextKeys.TERMINOLOGY_CONFIG);
		LinkedHashMap<String, String> categoryToPropertyKey =
				(LinkedHashMap<String, String>) context.getAttribute(ContextKeys.CATEGORY_TO_PROPERTY_KEY);

		HashSet<String> registerableCategories = ConfigurationReader.getAllCategoriesForBooleanAttribute(
				categoryToPropertyKey, terminologyConfig, ConfigKeys.CATEGORY_ATTRIBUTE_REGISTRABLE);
		request.setAttribute("registerableCategories", registerableCategories);

		request.getRequestDispatcher(JspViews.REGISTER_FOR_MEMBER_VIEW).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}
