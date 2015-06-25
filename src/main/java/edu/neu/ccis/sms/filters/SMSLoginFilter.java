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
 * This filter checks if the current user has a valid login session to access the requested resource; if there isn't
 * then forward the user to login screen to log him in with proper credentials.
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
    }

    /**
     * @see Filter#destroy()
     */
    @Override
    public void destroy() {
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

        /** check the session is expired or not */
        validateUserSession(httpRequest, httpResponse, chain);
    }

    /**
     * Except login.jsp and user_registration.jsp page requests all other resource requests should have a valid login
     * session; if not forward them to login page.
     * 
     * @param request
     *            - Http request
     * @param response
     *            - Http response
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

        // Except login.jsp and user_registration.jsp page requests all other resource requests should have a valid
        // login session
        boolean isSessionValid = session != null
                || session == null
                && (requestURL.contains("login.jsp") || requestURL.contains("user_registration.jsp") || requestURL
                        .equals("Login"));

        if (isSessionValid) {
            LOGGER.info("forwarding to resource!, valid session request!");
            chain.doFilter(request, response);
            return;
        } else {
            LOGGER.info("Invalid session, login again!");
            response.sendRedirect("/SMS/pages/login.jsp");
        }
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    @Override
    public void init(final FilterConfig fConfig) throws ServletException {
    }
}
