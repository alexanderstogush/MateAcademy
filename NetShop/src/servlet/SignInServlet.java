package servlet;

import dao.UserDao;
import model.User;
import org.apache.log4j.Logger;
import utils.HashUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class SignInServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SignInServlet.class);
    private static final UserDao USER_DAO = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String href = checkUser(username, req);

        req.getRequestDispatcher(href).forward(req, resp);
    }

    private String checkUser(String username, HttpServletRequest req) {
        Optional<User> userFromDB = USER_DAO.getUserByUsername(username);
        if (userFromDB.isPresent()) {
            return checkUserPassword(userFromDB.get(), req);
        }
        return "ErrorPage/user_missing.jsp";
    }

    private String checkUserPassword(User user, HttpServletRequest req) {
        String password = req.getParameter("password");
        String salt = user.getSalt();
        String hashFromPassword = HashUtil.getSHA512SecurePassword(password, salt);
        String hashFromDB = user.getPassword();

        if (hashFromPassword.equals(hashFromDB)) {
            return checkUserRole(user, req);
        }
        return "ErrorPage/wrong_pass.jsp";
    }

    private String checkUserRole(User user, HttpServletRequest req) {
        req.getSession().setAttribute("user", user);
        if (user.getRole().equals(User.ROLE.ADMIN)) {
            logger.debug("Admin with email " + user.getEmail() + " signed in");
            return "/admin";
        }
        logger.debug("User with email " + user.getEmail() + " signed in");
        return "/user";
    }
}
