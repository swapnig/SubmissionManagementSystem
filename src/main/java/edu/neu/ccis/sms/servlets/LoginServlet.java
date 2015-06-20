package edu.neu.ccis.sms.servlets;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.users.User;

/**
 * Servlet implementation class LoginServlet
 * 
 * @author Pramod R. Khare
 * @date 18-May-2015
 * @lastUpdate 3-June-2015
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    private final MessageDigest md;
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    /**
     * @throws NoSuchAlgorithmException
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() throws Exception {
        super();
        md = MessageDigest.getInstance("MD5");
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
    IOException
    {
        LOGGER.info("Method - LoginServlet:doPost");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        password = getMD5HashForString(password);

        UserDao userDao = new UserDaoImpl();
        User one = userDao.findUserByUsernameAndPassword(username, password);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            session = null;
        }

        if (one != null) {
            LOGGER.info("Login successful!!");
            session = request.getSession(true);

            // Save important information about user into session
            session.setAttribute(SessionKeys.keyUserObj, one);
            session.setAttribute(SessionKeys.keyUserId, one.getId());
            session.setAttribute(SessionKeys.keyUserName, one.getUsername());

            // TODO Fetch the Role Mappings and save them into the SessionKeys
            Set<UserToMemberMapping> userToMemberMappings = one.getUserToMemberMappings();
            session.setAttribute(SessionKeys.keyUserMemberMappings, userToMemberMappings);

            response.sendRedirect("/SMS/Dashboard");
        } else {
            LOGGER.info("Login failed!!");
            response.sendRedirect("pages/login.jsp");
        }
    }

    /**
     * All passwords are stored in the backend as MD5 hash hex-strings, so
     * converting the password from user into its hash -> hex string
     * 
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String getMD5HashForString(final String password) {
        md.update(password.getBytes());
        byte byteData[] = md.digest();
        // convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
