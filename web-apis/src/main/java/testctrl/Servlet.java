package testctrl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import testctrl.testmgmt.Generator;
import testctrl.testmgmt.QHeader;
import testctrl.testmgmt.Question;
import testctrl.testmgmt.THeader;
import testctrl.testmgmt.TMeta;
import testctrl.testmgmt.TestsDb;
import testctrl.testmgmt.UHeader;
import testctrl.testmgmt.WebDiv;

@WebServlet("/testctrl")
public class Servlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    Context _context;
    GitHubOAuthClient _githubOAuthClient;

    public static void checkTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("##Err##.TestCtrl: " + message);
        }
    }

    public static boolean isDefaultPort(String scheme, int port) {
        return (scheme.equalsIgnoreCase("http") && port == 80)
            || (scheme.equalsIgnoreCase("https") && port == 443);
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
        _githubOAuthClient = new GitHubOAuthClient(_context);
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
                    // http://localhost:8080/web-apis/testctrl?cmd=login&name=<name>
                    answer = executeCmdLogin(request, params);
                    break;
                case "oauth":
                    // http://localhost:8080/web-apis/testctrl?cmd=oauth&code=<handshake_uuid_backend>&iss=<metadata>&state=<handshake_uuid_frontend>
                    executeCmdOAuth(request, response, params);
                    return;
                case "logout":
                    // http://localhost:8080/web-apis/testctrl?cmd=logout
                    answer = executeCmdLogout(httpSession);
                    break;
                case "status":
                    // http://localhost:8080/web-apis/testctrl?cmd=status&op=log
                    answer = executeCmdStatus(httpSession, params);
                    break;
                case "set":
                    // http://localhost:8080/web-apis/testctrl?cmd=set&op=vtest&name=<test-name>&args=<qid1,qid2,...>
                    answer = executeCmdSet(httpSession, params);
                    break;
                case "query":
                    // http://localhost:8080/web-apis/testctrl?cmd=query&op=qset
                    // http://localhost:8080/web-apis/testctrl?cmd=query&op=tset
                    // http://localhost:8080/web-apis/testctrl?cmd=query&op=question&qid=<name>
                    // http://localhost:8080/web-apis/testctrl?cmd=query&op=answer&qid=<name>
                    // http://localhost:8080/web-apis/testctrl?cmd=query&op=test&tid=<name>
                    // http://localhost:8080/web-apis/testctrl?cmd=query&op=uset
                    // http://localhost:8080/web-apis/testctrl?cmd=query&op=user&uid=<name>
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
        String name = params.get("name")[0].trim();
        User user = _context.getUser(name);
        checkTrue(user != null && user.hasRole("admin","teacher"), "Invalid name, role or GitHub handle!");

        // save into the session a handshake_uuid for frontend and the expect GitHub handle to authenticate!
        String state = UUID.randomUUID().toString();
        httpSession.setAttribute("oauth_state", state);
        httpSession.setAttribute("oauth_ghHandle", user.username);

        // extract the origin of the request URL, such that we can use the same for the redirect URL ()
        String requestScheme = request.getScheme();
        String requestHost = request.getServerName();
        String requestPort = isDefaultPort(requestScheme, request.getServerPort()) ? "" : ":" + request.getServerPort();
        String redirectUri = String.format("%s://%s%s%s", requestScheme, requestHost, requestPort, _context.getConfig().github_redirect_uri);
        httpSession.setAttribute("oauth_redirect_uri", redirectUri);

        // build the github authorization URL, which includes the redirect command. When GitHub auth is done, browser is going to use the redirectURL to call back with cmd=oauth!
        // Note: we do not give GitHub any indication of which handle/user to login!
        String authorizeUrl = _githubOAuthClient.buildAuthorizeUrl(redirectUri, state);
        // redirectUrl = "https://github.com/login/oauth/authorize
        //                ?client_id=Ov23liug3HoGm26w66n2 // OAuth client_id as registered in GitHub for TestCtrl app
        //                &redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fweb-apis%2Ftestctrl%3Fcmd%3Doauth // "http://localhost:8080/web-apis/testctrl?cmd=oauth"
        //                &scope=read%3Auser // read:user
        //                &state=4ce58cdc-fd48-46b6-b123-c1a8fdca38ac" // random UUID, handshake w/ GitHub

        // the answer contains the redirect URL which is going to be executed by the frontend
        return new Answer().new Redirect(authorizeUrl, "Redirecting to GitHub OAuth...");
    }

    @SuppressWarnings("null")
    public void executeCmdOAuth(HttpServletRequest request, HttpServletResponse response, Map<String, String[]> params) throws ServletException, IOException, NoSuchAlgorithmException {
        String targetUrl = "";
        String expectedHandle = "";
        String oauthErr = "";

        try {
            // check the context is valid
            checkTrue(_context.isValid(), oauthErr = "Backend configuration corrupted or invalid!");

            // check expected parameters exist
            checkTrue(params.containsKey("code"), oauthErr = "Missing 'code' parameter!");
            checkTrue(params.containsKey("state"), oauthErr = "Missing 'state' parameter!");

            // check the session is valid
            HttpSession httpSession = request.getSession();
            String expectedState = (String) httpSession.getAttribute("oauth_state");
            expectedHandle = (String) httpSession.getAttribute("oauth_ghHandle");
            String redirectUrl = (String) httpSession.getAttribute("oauth_redirect_uri");
            checkTrue(expectedState != null && expectedHandle != null && redirectUrl != null, oauthErr = "Invalid Oauth session!");

            // check the handshake_uuid with the frontend matches what we saved previously
            String state = params.get("state")[0];
            checkTrue(expectedState.equals(state), oauthErr = "Invalid OAuth state!");

            // check the GitHub login that got authenticated matches the expected handle
            String code = params.get("code")[0];
            String githubLogin = _githubOAuthClient.getGitHubLogin(redirectUrl, code, state);
            checkTrue(githubLogin != null && !githubLogin.isEmpty(), oauthErr = "GitHub OAuth failed!");
            checkTrue(githubLogin.equalsIgnoreCase(expectedHandle), oauthErr = "GitHub handle mismatch!");

            // check the User for this login exists and is valid
            User user = _context.getUser(githubLogin);
            checkTrue(user != null && user.hasRole("admin","teacher"), oauthErr = "Unrecognized GitHub handle or invalid role!");

            // finally create a session for this user
            String rootUrl = String.format("%s://%s:%s%s", request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
            Session session = _context.newSession(user, httpSession, rootUrl);
            _context.Log(new LogEntry("User '%s' logged in via GitHub OAuth session [%s]", user.username, session.getId()));

            // cleanup and return the redirect to the main portal
            httpSession.removeAttribute("oauth_state");
            httpSession.removeAttribute("oauth_ghHandle");
            // Note: the redirect may be different in the future, based on the role of this user
            targetUrl = String.format("%s/testctrl/adminPanel.jsp?sid=%s&name=%s&ver=2.1", request.getContextPath(), session.getId(), user.username);
        } catch (Exception e) {
            targetUrl = String.format("%s/testctrl/login.jsp?name=%s&err=%s&ver=2.1", request.getContextPath(), expectedHandle, GitHubOAuthClient.urlEncode(oauthErr));
        }
        response.sendRedirect(targetUrl);
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
        String type = params.get("op")[0];
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
            case "vtest":
                // http://localhost:8080/web-apis/testctrl?cmd=set&op=vtest&name=<testName>&qlist=<test1,test2,...>
                checkTrue(params.containsKey("name"), "Missing 'name' parameter!");
                checkTrue(params.containsKey("qlist"), "Missing 'qlist' parameter!");
                String testName = params.get("name")[0];
                String testQList = params.get("qlist")[0].trim();
                checkTrue(!testName.isEmpty(), "Invalid (empty) test name!");
                String[] testQIDs = testQList.isEmpty() ? new String[0] : testQList.split(",");
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
        checkTrue(params.containsKey("op"), "Missing 'op' parameter!");
        checkTrue(session.getUser().hasRole("admin","teacher"), "Access denied!");
        session.touch();
        String type = params.get("op")[0];
        Generator gen = _context.getGenerator();
        TestsDb testsDb = _context.getTestsDb();
        WebDiv webDiv = _context.getWebDiv();

        switch(type) {
            case "qset":
                // http://localhost:8080/web-apis/testctrl?cmd=query&op=qset
                Collection<QHeader> qRecs = gen.getQRecs();
                _context.Log(new LogEntry("[query:qset] Returning %d question records", qRecs.size()));
                return new Answer().new QList(qRecs);
            case "question":
                // http://localhost:8080/web-apis/testctrl?cmd=query&op=question&qid=<question>
            case "answer":
                // http://localhost:8080/web-apis/testctrl?cmd=query&op=answer&qid=<question>
                String qID = params.get("qid")[0];
                boolean isAnswer = type.equalsIgnoreCase("answer");
                Question q = gen.getQuestion(qID);
                return (q != null)
                    ? new Answer().new QData(q.getQHeader(), webDiv.getDiv(q, isAnswer))
                    : new Answer().new Err("Unknown question '%s'", qID);
            case "tset":
                // http://localhost:8080/web-apis/testctrl?cmd=query&op=tset
                Collection<THeader> tRecs = testsDb.getTHeaders();
                _context.Log(new LogEntry("[query:tset] Returning %d test records", tRecs.size()));
                return new Answer().new TList(tRecs);
            case "test":
                // http://localhost:8080/web-apis/testctrl?cmd=query&op=test&tid=<name>
                String tID = params.get("tid")[0];
                TMeta t = testsDb.getTMeta(tID);
                String testPath = String.format("%s/%s/%s", session.getRootUrl(), _context.getConfig().tests_root, tID);
                return (t != null)
                    ? new Answer().new TData(t, testPath)
                    : new Answer().new Err("Unknown test '%s'", tID);
            case "uset":
                // http://localhost:8080/web-apis/testctrl?cmd=query&op=uset
                Collection<UHeader> uRecs = _context.getUHeaders();
                _context.Log(new LogEntry("[query:uset] Returning %d user records", uRecs.size()));
                return new Answer().new UList(uRecs);
            case "user":
                return new Answer().new Err("Unsupported 'user' operation!");
            default:
                return new Answer().new Err("Unknown query operation!");
        }
    }
}
