package edu.neu.ccis.sms.filters;

import java.io.IOException;
import java.util.logging.Logger;

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

/**
 * Servlet Filter implementation class SMSLoginFilter
 * 
 * @author Pramod R. Khare
 * @date 23-May-2015
 * @lastUpdate 3-June-2015
 */
@WebFilter("/*")
public class SMSLoginFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(SMSLoginFilter.class.getName());

    /**
     * Default constructor.
     */
    public SMSLoginFilter() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see Filter#destroy()
     */
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // check the session is expired or not
        validateUserSession(httpRequest, httpResponse, chain);
    }

    /**
     * validateUserSession checks for session expired request and returns expired message.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void validateUserSession(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) throws IOException, ServletException
    {
        LOGGER.info("Method - SMSLoginFilter:validateUserSession");

        String requestURL = request.getRequestURI();
        requestURL = requestURL.substring(requestURL.lastIndexOf("/") + 1);

        HttpSession session = request.getSession(false);
        boolean isSessionValid = session != null
                || session == null
                && (requestURL.contains("login.jsp") || requestURL.contains("user_registration.jsp") || requestURL
                        .equals("Login"));

        // If the request from the login page pass to login action
        // to create new user session.
        if (isSessionValid) {
            LOGGER.info("forwarding to resource!, valid session request!");
            chain.doFilter(request, response);
            return;
        }
        // If the request for other than login page then check whether the
        // request has a session expired or not
        else {
            LOGGER.info("Invalid session, login again!");
            // HttpServletResponse.SC_LENGTH_REQUIRED 411 error code here used
            // to indicated for session expiration.
            response.sendRedirect("/SMS/pages/login.jsp");
        }
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    @Override
    public void init(final FilterConfig fConfig) throws ServletException {
        // TODO Auto-generated method stub
    }

}
