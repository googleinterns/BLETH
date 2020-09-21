package servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/welcome-msg")
public class WelcomeMessageServlet extends HttpServlet {

    private List<String> optionalMessages;

    @Override
    public void init() {
        optionalMessages = new ArrayList<>();
        optionalMessages.add("Hello!");
        optionalMessages.add("Welcome!");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String msg = optionalMessages.get((int)
                (Math.random() * optionalMessages.size()));
        response.setContentType("text/html;");
        response.getWriter().println(msg);
    }
}
