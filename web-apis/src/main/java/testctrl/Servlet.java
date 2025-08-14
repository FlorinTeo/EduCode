package testctrl;

import java.io.IOException;
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

     /**
     * On initialization retrieve and retain _serverContext 
     */
    public void init() throws ServletException {
        _context = (Context) getServletContext().getAttribute("context-testctrl");
    }

    private static void checkTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("##Err##: " + message);
        }
    }

    /**
     * Parse a "http://.../web-apis/testctrl?" request
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> params = request.getParameterMap();
        String sid = "?";
        Answer answer = new Answer();

        try {
            checkTrue(params.containsKey("cmd"),"Missing 'cmd' parameter!");
            String cmd = params.get("cmd")[0];
            switch(cmd.toLowerCase()) {
                case "login": // http://localhost:8080/web-apis/testctrl?cmd=login&name=<name>&hash=<pwd-hash>]
                    checkTrue(_context.isReady(), "Server not ready!");
                    answer = answer.new Msg(sid, "Session created!");
                    break;
                case "logout": // http://localhost:8080/web-apis/testctrl?cmd=logout
                    answer = answer.new Msg(sid, "Session closed!");
                    break;
                default:
                    answer = answer.new Err("Unsupported 'cmd' parameter!");
            }
        } catch(RuntimeException e) {
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
