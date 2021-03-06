package com.shop.servlet.admin.crud.user;

import com.shop.dao.RoleDao;
import com.shop.dao.UserDao;
import com.shop.dao.implamentation.hibernate.RoleDaoHibImpl;
import com.shop.dao.implamentation.hibernate.UserDaoHibImpl;
import com.shop.model.Role;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/admin/user/add")
public class AddUserServlet extends HttpServlet {

    private static final UserDao userDao = new UserDaoHibImpl();
    private static final RoleDao roleDao = new RoleDaoHibImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String href = checkUser(username, req);

        req.getRequestDispatcher(href).forward(req, resp);
    }

    private String checkUser(String username, HttpServletRequest req) {
        if (userDao.getUserByUsername(username).equals(Optional.empty())) {
            return checkEmail(req);
        }
        return "ErrorPage/user_is_already_registred.jsp";
    }

    private String checkEmail(HttpServletRequest req) {
        String email = req.getParameter("email");
        Pattern emailPattern = Pattern.compile("^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$");
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.find()) {
            return checkPassword(req);
        }
        return "ErrorPage/wrong_email.jsp";
    }

    private static String checkPassword(HttpServletRequest req) {
        String password = req.getParameter("password");
        String repeatPass = req.getParameter("repeatPassword");

        if (password.equals(repeatPass)) {
            String username = req.getParameter("username");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String email = req.getParameter("email");
            String isAdmin = req.getParameter("is_admin");
            String isUser = req.getParameter("is_user");
            Role adminRole = roleDao.getByName("ADMIN").get();
            Role userRole = roleDao.getByName("USER").get();

            Set<Role> roles = new HashSet<>();

            if (isAdmin != null && isAdmin.equals("true")) {
                roles.add(adminRole);
            }
            roles.add(userRole);

            User user = new User(username, firstName, lastName, email, password, roles);
            userDao.add(user);

            return "/admin";
        }
        return "ErrorPage/invalid_repeated_password.jsp";
    }
}
