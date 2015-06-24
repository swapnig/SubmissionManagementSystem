package edu.neu.ccis.sms.servlets;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.JspViews;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.users.User;

/**
 * Servlet implementation class RegisterUser
 * 
 * @author Pramod R. Khare
 * @date 28-May-2015
 * @lastUpdate 1-June-2015
 */
@WebServlet("/RegisterUser")
public class RegisterUser extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MessageDigest md;
    private static final Logger LOGGER = Logger.getLogger(RegisterUser.class.getName());

    /**
     * @throws Exception
     * @see HttpServlet#HttpServlet()
     */
    public RegisterUser() throws Exception {
        super();
        md = MessageDigest.getInstance("MD5");
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        LOGGER.info("Method - RegisterUser:doPost");

        String username = request.getParameter("userName");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String emailId = request.getParameter("email");
        String firstname = request.getParameter("firstName");
        String lastname = request.getParameter("lastName");

        if(!confirmPassword.equals(password)){
            request.setAttribute(RequestKeys.PARAM_MESSAGE, "Passwords doesn't match, please provide both passwords the same and retry!");
            request.getRequestDispatcher(JspViews.REGISTER_USER_VIEW).forward(request, response);
            return;
        }

        // Convert password into its MD5Hash
        password = getMD5HashForString(password);

        UserDao userDao = new UserDaoImpl();
        User one = new User();
        one.setFirstname(firstname);
        one.setLastname(lastname);
        one.setPassword(password);
        one.setEmail(emailId);
        one.setUsername(username);
        userDao.saveUser(one);

        LOGGER.info("User registered successfully! - " + emailId);
        request.setAttribute(RequestKeys.PARAM_MESSAGE, "User registered successfully. Please login to start using SMS!");
        // redirects client to message page
        request.getRequestDispatcher(JspViews.REGISTER_USER_VIEW).forward(request, response);
    }

    /**
     * All passwords are stored in the backend as MD5 hash hex-strings, so converting the password from user into its
     * hash -> hex string
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
