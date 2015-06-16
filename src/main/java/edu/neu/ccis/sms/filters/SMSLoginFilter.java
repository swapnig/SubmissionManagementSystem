package edu.neu.ccis.sms.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.neu.ccis.sms.constants.SessionKeys;

/**
 * Servlet Filter implementation class SMSLoginFilter
 * 
 * @author Pramod R. Khare
 * @date 23-May-2015
 * @lastUpdate 3-June-2015
 */
@WebFilter("/*")
public class SMSLoginFilter implements Filter {

    /**
     * Default constructor.
     */
    public SMSLoginFilter() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
        // TODO Auto-generated method stub
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // check the session is expired or not
        validateUserSession(httpRequest, httpResponse, chain);
    }

    /**
     * validateUserSession checks for session expired request and returns
     * expired message.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void validateUserSession(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String requestURL = request.getRequestURI();
        requestURL = requestURL.substring(requestURL.lastIndexOf("/") + 1);

        System.out.println("Requested URL - " + requestURL);
        HttpSession session = request.getSession(false);
        System.out.println("session..." + session);

        if (session != null) {
            System.out.println("UserObj - " + session.getAttribute(SessionKeys.keyUserObj));
            System.out.println("Session exists");
        } else {
            System.out.println("session is null");
        }

        boolean isSessionValid = (session != null || (session == null && (requestURL.contains("login.jsp")
                || requestURL.contains("user_registration.jsp") || requestURL.equals("Login"))));

        // If the request from the login page pass to login action
        // to create new user session.
        if (isSessionValid) {
            System.out.println("forwarding to resource!");
            chain.doFilter(request, response);
            return;
        }
        // If the request for other than login page then check whether the
        // request has a session expired or not
        else {
            System.out.println("Invalid session...");
            // HttpServletResponse.SC_LENGTH_REQUIRED 411 error code here used
            // to indicated for session expiration.
            response.sendRedirect("login.jsp");
        }
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
        // TODO Auto-generated method stub
    }

}
