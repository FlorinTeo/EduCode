package testctrl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/testctrl")
public class Servlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    Context _context;

    public static void checkTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("##Err##.TestCtrl: " + message);
        }
    }

    /**
     * On initialization retrieve and retain _serverContext 
     */
    public void init() throws ServletException {
        _context = (Context) getServletContext().getAttribute("context-testctrl");
    }

    /**
     * Parse a "http://.../web-apis/testctrl?" request
     */
    @SuppressWarnings("null")
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the HttpSession (creates one if it doesn't exist)

        Map<String, String[]> params = request.getParameterMap();
        Session session = null;
        Answer answer = new Answer();

        try {
            checkTrue(_context.isReady(), "Server not ready!");
            checkTrue(params.containsKey("cmd"),"Missing 'cmd' parameter!");
            String cmd = params.get("cmd")[0];
            switch(cmd.toLowerCase()) {
                case "login": // http://localhost:8080/web-apis/testctrl?cmd=login&name=<name>&pwd=<password>]
                    checkTrue(params.containsKey("name"), "Missing 'name' parameter!");
                    checkTrue(params.containsKey("pwd"), "Missing 'pwd' parameter!");
                    String name = params.get("name")[0];
                    String pwd = params.get("pwd")[0];                   
                    // try to create a session. This may throw if user is invalid (unknown or wrong password) or if a session is already opened.
                    session = _context.newSession(name, pwd, request.getSession());
                    answer = answer.new Msg(session.getId(), "Session created!");
                    break;
                case "logout": // http://localhost:8080/web-apis/testctrl?cmd=logout
                    session = _context.closeSession(request.getSession());
                    answer = answer.new Msg(session.getId(), "Session closed!");
                    break;
                default:
                    answer = answer.new Err("Unsupported 'cmd' parameter!");
            }
        } catch(RuntimeException | NoSuchAlgorithmException e) {
            answer = answer.new Err(e.getMessage());
        }

        String jsonAnswer = answer.toString();
        if (answer instanceof Answer.Err) {
            response.setStatus(400);
        }
        response.setContentType("application/json");
        response.getOutputStream().print(jsonAnswer);
    }
}
