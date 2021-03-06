package com.shop.servlet;

import com.shop.dao.UserDao;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@WebServlet("/SignUp")
public class SignUpServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        signUp(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    private static void signUp(HttpServletRequest req, HttpServletResponse resp) {
        req.getSession().setAttribute("admin", null);
        UserDao userDao = new UserDao();

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String repeatPass = req.getParameter("repeatPassword");

        try {
            if (userDao.getUserByUsername(username).equals(Optional.empty())) {
                if (checkEmail(email)) {
                    if (checkPassword(password, repeatPass)) {
                        userDao.addUser(new User(username, firstName, lastName, email, password, "USER"));
                        req.setAttribute("username", username);
                        req.getRequestDispatcher("UserStartPage.jsp").forward(req, resp);
                    } else {
                        req.getRequestDispatcher("ErrorPage/ErrorPasswordPage.jsp").forward(req, resp);
                    }
                } else {
                    req.getRequestDispatcher("ErrorPage/ErrorEmailPage.jsp").forward(req, resp);
                }
            } else {
                req.getRequestDispatcher("ErrorPage/UserIsRegistred.jsp").forward(req, resp);
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkPassword(String password, String repeatPassword) {
        if (password.equals(repeatPassword)) {
            return true;
        }
        return false;
    }

    private static boolean checkEmail(String email) {
        Pattern emailPattern = Pattern.compile("^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$");
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
}
