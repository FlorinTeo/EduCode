package testctrl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
     * Parse a "http://.../web-apis/testctrl?" request and dispatch it to the specific
     * executeCmdXXX method.
     */
    @SuppressWarnings("null")
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Answer answer;
        try {
            HttpSession httpSession = request.getSession();
            Map<String, String[]> params = request.getParameterMap();
            checkTrue(_context.isReady(), "Server not ready!");
            checkTrue(params.containsKey("cmd"),"Missing 'cmd' parameter!");
            String cmd = params.get("cmd")[0];
            switch(cmd.toLowerCase()) {
                case "login":
                    // http://localhost:8080/web-apis/testctrl?cmd=login&name=<name>&pwd=<password>]
                    answer = executeCmdLogin(httpSession, params);
                    break;
                case "logout":
                    // http://localhost:8080/web-apis/testctrl?cmd=logout
                    answer = executeCmdLogout(httpSession);
                    break;
                default:
                    answer = new Answer().new Err("Unsupported 'cmd' parameter!");
            }
        } catch(RuntimeException | NoSuchAlgorithmException e) {
            answer = new Answer().new Err(e.getMessage());
        }

        // convert the answer to JSON and send it back
        String jsonAnswer = answer.toString();
        if (answer instanceof Answer.Err) {
            response.setStatus(400);
        }
        response.setContentType("application/json");
        response.getOutputStream().print(jsonAnswer);
    }

    public Answer executeCmdLogin(HttpSession httpSession, Map<String, String[]> params) throws NoSuchAlgorithmException {
        checkTrue(params.containsKey("name"), "Missing 'name' parameter!");
        checkTrue(params.containsKey("pwd"), "Missing 'pwd' parameter!");
        String name = params.get("name")[0];
        String pwd = params.get("pwd")[0];                   
        // try to create a session. This may throw if user is invalid (unknown or wrong password) or if a session is already opened.
        Session session = _context.newSession(name, pwd, httpSession);
        return new Answer().new Msg(session.getId(), "Session created!");
    }

    public Answer executeCmdLogout(HttpSession httpSession) {
        Session session = _context.closeSession(httpSession);
        return new Answer().new Msg(session.getId(), "Session closed!");
    }
}
