package testctrl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import testctrl.testmgmt.Generator;
import testctrl.testmgmt.QHeader;
import testctrl.testmgmt.Question;
import testctrl.testmgmt.WebDiv;

@WebServlet("/testctrl")
public class Servlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    Context _context;

    public static void checkTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("##Err##.TestCtrl: " + message);
        }
    }

    public static String paramsToLog(Map<String, String[]> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            String param = entry.getKey();
            String value = param.equalsIgnoreCase("pwd") ? "******" : entry.getValue() != null && entry.getValue().length > 0 ? entry.getValue()[0] : "";
            sb.append(param).append("=").append(value);
        }
        return sb.toString();
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession httpSession = request.getSession();
        Map<String, String[]> params = request.getParameterMap();
        Answer answer;
        try {
            checkTrue(_context.isReady(), "Server not ready!");
            checkTrue(params.containsKey("cmd"),"Missing 'cmd' parameter!");
            String cmd = params.get("cmd")[0];
            switch(cmd.toLowerCase()) {
                case "login":
                    // http://localhost:8080/web-apis/testctrl?cmd=login&name=<name>&pwd=<password>]
                    answer = executeCmdLogin(request, params);
                    break;
                case "logout":
                    // http://localhost:8080/web-apis/testctrl?cmd=logout
                    answer = executeCmdLogout(httpSession);
                    break;
                case "status":
                    // http://localhost:8080/web-apis/testctrl?cmd=status&type=log
                    answer = executeCmdStatus(httpSession, params);
                    break;
                case "set":
                    // http://localhost:8080/web-apis/testctrl?cmd=set&op=setusr&name=<name>&pwd=<password>
                    // http://localhost:8080/web-apis/testctrl?cmd=set&op=vtest&name=<test-name>&args=<qid1,qid2,...>
                    answer = executeCmdSet(httpSession, params);
                    break;
                case "query":
                    // http://localhost:8080/web-apis/testctrl?cmd=query&type=qset
                    // http://localhost:8080/web-apis/testctrl?cmd=query&type=qtest&qid=<name>
                    // http://localhost:8080/web-apis/testctrl?cmd=query&type=qanswer&qid=<name>
                    answer = executeCmdQuery(httpSession, params);
                    break;
                default:
                    answer = new Answer().new Err("Unsupported 'cmd' parameter!");
            }
        } catch(RuntimeException | NoSuchAlgorithmException e) {
            _context.Log(new LogEntry("%s > ?%s", e.getMessage(), paramsToLog(params)));
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

    @SuppressWarnings("null")
    public Answer executeCmdLogin(HttpServletRequest request, Map<String, String[]> params) throws NoSuchAlgorithmException {
        HttpSession httpSession = request.getSession();
        checkTrue(params.containsKey("name"), "Missing 'name' parameter!");
        checkTrue(params.containsKey("pwd"), "Missing 'pwd' parameter!");
        String name = params.get("name")[0];
        String pwd = params.get("pwd")[0];
        User user = _context.getUser(name);
        checkTrue(user != null && user.hasRole("admin","teacher") && user.matchesPwd(pwd), "Invalid name, role or password!");
        // try to create a session. This may throw if another user is logged in this session already.
        String rootUrl = String.format("%s://%s:%s/%s", request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
        Session session = _context.newSession(user, httpSession, rootUrl);
        _context.Log(new LogEntry("User '%s' logged in session [%s]", user.username, session.getId()));
        return new Answer().new Msg(session.getId(), "Session created!");
    }

    public Answer executeCmdLogout(HttpSession httpSession) {
        Session session = _context.closeSession(httpSession);
        _context.Log(new LogEntry("User '%s' logged out from session [%s]", session.getUser().username, session.getId()));
        return new Answer().new Msg(session.getId(), "Session closed!");
    }

    @SuppressWarnings("null")
    public Answer executeCmdStatus(HttpSession httpSession, Map<String, String[]> params) {
        Session session = _context.getSession(httpSession);
        checkTrue(session != null, "Session not found!");
        session.touch();
        String type = params.get("type")[0];
        switch(type) {
            case "log":
                // http://localhost:8080/web-apis/testctrl?cmd=status&type=log
                return new Answer().new Logs(session.purgeLog());
            default:
                return new Answer().new Err("Unknown status type!");
        }
    }

    @SuppressWarnings("null")
    public Answer executeCmdSet(HttpSession httpSession, Map<String, String[]> params) throws NoSuchAlgorithmException, IOException {
        Session session = _context.getSession(httpSession);
        checkTrue(session != null, "Session not found!");
        checkTrue(params.containsKey("op"), "Missing 'op' parameter!");
        String op = params.get("op")[0];
        switch(op) {
            case "setusr":
                // http://localhost:8080/web-apis/testctrl?cmd=set&op=setusr&name=<name>&pwd=<password>
                checkTrue(params.containsKey("name"), "Missing 'name' parameter!");
                checkTrue(params.containsKey("pwd"), "Missing 'pwd' parameter!");
                String name = params.get("name")[0];
                String pwd = params.get("pwd")[0];
                User targetUser = _context.getUser(name);
                checkTrue(targetUser != null, "Invalid user!");
                if (session.getUser().hasRole("admin","teacher")) {
                    // admins and teachers are allowed to change password for any user
                    checkTrue(targetUser.setPwd(pwd), "Failed to change password!");
                    checkTrue(_context.saveConfig(), "Failed to save configuration!");
                    Answer.Msg msgAnswer = new Answer().new Msg(session.getId(),
                        "User '%s' changed password for user '%s'!",
                        session.getUser().username,
                        targetUser.username);
                    _context.Log(new LogEntry(msgAnswer._message));
                    return msgAnswer;
                } else {
                    checkTrue(targetUser.equals(session.getUser()), "Invalid non-self user!");
                    // non-admins/teachers are only allowed to change only their own password
                    checkTrue(targetUser.setPwd(pwd), "Failed to change password!");
                    checkTrue(_context.saveConfig(), "Failed to save configuration!");
                    Answer.Msg msgAnswer = new Answer().new Msg(session.getId(),
                        "User '%s' changed own password!",
                        session.getUser().username);
                    _context.Log(new LogEntry(msgAnswer._message));
                    return msgAnswer;
                }
            case "vtest":
                // http://localhost:8080/web-apis/testctrl?cmd=set&op=vtest&name=<testName>&args=<test1,test2,...>
                checkTrue(params.containsKey("name"), "Missing 'name' parameter!");
                checkTrue(params.containsKey("args"), "Missing 'args' parameter!");
                String testName = params.get("name")[0];
                String testArgs = params.get("args")[0].trim();
                checkTrue(!testName.isEmpty(), "Invalid (empty) test name!");
                String[] testQIDs = testArgs.isEmpty() ? new String[0] : testArgs.split(",");
                WorkVerTest wVerTest = new WorkVerTest(session, testName, testQIDs);
                _context.QueueWork(wVerTest);
                Answer.Msg msgAnswer = new Answer().new Msg(session.getId(),
                    "User '%s' initiated vtest '%s' changes..",
                    session.getUser().username,
                    testName);
                _context.Log(new LogEntry(msgAnswer._message));
                return msgAnswer;
            default:
                return new Answer().new Err("Unknown set operation '" + op + "'!");
        }
    }

    @SuppressWarnings("null")
    public Answer executeCmdQuery(HttpSession httpSession, Map<String, String[]> params) throws NoSuchAlgorithmException, IOException {
        Session session = _context.getSession(httpSession);
        checkTrue(session != null, "Session not found!");
        checkTrue(params.containsKey("type"), "Missing 'type' parameter!");
        checkTrue(session.getUser().hasRole("admin","teacher"), "Access denied!");
        session.touch();
        String type = params.get("type")[0];
        Generator gen = _context.getGenerator();
        WebDiv webDiv = _context.getWebDiv();

        switch(type) {
            case "qset":
                // http://localhost:8080/web-apis/testctrl?cmd=query&type=qset
                Collection<QHeader> qRecs = gen.getQRecs();
                _context.Log(new LogEntry("[query:qset] Returning %d question records", qRecs.size()));
                return new Answer().new QList(qRecs);
            case "qanswer":
                // http://localhost:8080/web-apis/testctrl?cmd=query&type=qtest&qid=<question>
            case "qtest":
                // http://localhost:8080/web-apis/testctrl?cmd=query&type=qanswer&qid=<question>
                String qID = params.get("qid")[0];
                boolean isAnswer = type.equalsIgnoreCase("qanswer");
                Question q = gen.getQuestion(qID);
                return new Answer().new QDiv(q.getQHeader(), webDiv.getDiv(q, isAnswer));
            default:
                return new Answer().new Err("Unknown query type!");
        }
    }
}
