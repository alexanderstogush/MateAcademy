package servlet.CRUD;

import dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = "/DeleteUser")
public class DeleteUserServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDao userDao = new UserDao();

        int id = Integer.parseInt(req.getParameter("user_id"));
        userDao.deleteUser(id);

        req.setAttribute("users", userDao.getAllUsers());
        req.getRequestDispatcher("StartPage.jsp").forward(req, resp);
    }
}
