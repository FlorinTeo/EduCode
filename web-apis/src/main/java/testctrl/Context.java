package testctrl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import testctrl.testmgmt.Generator;
import testctrl.testmgmt.TestsDb;
import testctrl.testmgmt.WebDiv;

public class Context extends TimerTask {
    private final static int _DELAY_START = 8; // trigger servlet initialization asynchronously, with 8ms delay
    private final static int _HEARTBEAT_INTERVAL = 1000; // 1sec heart beat
    private final static int _HEARTBEATS_PRUNNING = 10; // prunning happens every 10 x 1sec
    private final static int _HEARTBEATS_WORKING = 1; // async work is checked every 1 x 1sec

    // #region: [Public] Enum & Class definitions pertaining to TestCtrl context.
    public enum State {
        INITIALIZING,
        READY,
        CLEANING,
        WORKING,
        CLOSING,
        STOPPED
    }

    public class Config {
        public String tests_root;
        public String users_path;
        public List<User> users;
    };
    // #endregion: [Public] Enum & Class definitions pertaining to TestCtrl context.

    // #region: [Private] Instance variables for TestCtrl context.
    private int _hbCounterPrunning;
    private int _hbCounterWorking;
    private ServletContext _servletContext;
    private Config _config;
    // Map of sessions keyed by their session ID.
    private Map<HttpSession, Session> _sessions;
    private State _state;
    private Timer _timer;
    // Test management fields
    private Generator _generator;
    private TestsDb _testsDb;
    private WebDiv _webDiv;
    // Work management fields
    private Queue<Work> _queueWork;
    // #endregion: [Private] Instance variables for TestCtrl context.

    public Context(ServletContext servletContext) {
        _hbCounterPrunning = 0;
        _hbCounterWorking = 0;
        _servletContext = servletContext;
        _config = null;
        _sessions = new HashMap<HttpSession, Session>();
        _state = State.INITIALIZING;
        _timer = new Timer();
        // in 8ms load the servlet, then every minute perform timer-based operations!
        _timer.schedule(this, _DELAY_START, _HEARTBEAT_INTERVAL);
        _queueWork = new LinkedList<Work>();
    }

    // #region: [Public] Life-cycle methods
    public boolean isReady() {
        synchronized(_state) {
            return _state == State.READY;
        }
    }

    public State getState() {
        return _state;
    }

    // #region: [Public] Configuration management methods
    public boolean loadConfig() throws IOException {
        Gson deserializer = new Gson();

        // get the root path of the servlet (i.e "C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps\web-apis")
        String contextRoot = _servletContext.getRealPath(".");

        // deserialize the main config object into _config
        Path configPath = Paths.get(contextRoot).resolve("WEB-INF/classes/testctrl/res/testctrl-config.json").normalize();
        String configString = Files.readString(configPath);
        _config = deserializer.fromJson(configString, Config.class);
        
        // deserialize the userDb, from the _config.users_path into _config.users
        Path userDbPath = Paths.get(contextRoot).resolve(_config.users_path).normalize();
        if (!Files.exists(userDbPath)) {
            userDbPath = Paths.get(contextRoot).resolve("WEB-INF/classes/testctrl/res/testctrl-users.json").normalize();
        }
        String userDbString = Files.readString(userDbPath);
        Type userListType = new TypeToken<List<User>>(){}.getType();
        _config.users = deserializer.fromJson(userDbString, userListType);

        return true;
    }

    public Config getConfig() {
        return _config;
    }

    public boolean saveConfig() throws IOException {
        Gson serializer = new GsonBuilder().setPrettyPrinting().create();

        // get the root path of the servlet (i.e "C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps\web-apis")
        String contextRoot = _servletContext.getRealPath(".");

        // serialize the userDb from _config.users into the _confg.users_path location
        Path userDbPath = Paths.get(contextRoot).resolve(_config.users_path).normalize();
        String userDbString = serializer.toJson(_config.users);
        Files.writeString(userDbPath, userDbString);

        // serialize the main config object from _config
        Path configPath = Paths.get(contextRoot).resolve("WEB-INF/classes/testctrl/res/testctrl-config.json").normalize();
        String configString = serializer.toJson(_config);
        Files.writeString(configPath, configString);
        return true;
    }
    // #endregion: [Public] Configuration management methods

    public User getUser(String username) {
        return _config.users.stream().filter(u -> u.username.equals(username)).findFirst().orElse(null);
    }

    public void closing() {
        synchronized(_state) {
            _state = State.CLOSING;
            System.out.printf("~~~~ TestCtrl context state: %s ~~~~\n", _state.name());
        }

        // stop any timer-based operation
        _timer.cancel();

        synchronized(_state) {
            _state = State.STOPPED;
            System.out.printf("~~~~ TestCtrl context state: %s ~~~~\n", _state.name());
        }
    }
    // #endregion: [Public] Life-cycle methods

    // #region: [Private] Async runners
    @Override
    public void run() {
        switch(_state) {
        case INITIALIZING:
            // initialization code, running asynchronously after servlet is activated
            try {
                runInitialize();
            } catch (IOException | NoSuchAlgorithmException e) {
                System.out.printf("##ERR##.TestCtrl:  initialization failure: %s\n", e.getMessage());
                e.printStackTrace();
            }
            break;
        case READY:
            // timer-based operations while the server is ready
            runHeartbeat();
            break;
        default:
            // no action on any of the other states!
            break;
        }
    }

    private void runInitialize() throws IOException, NoSuchAlgorithmException {
        synchronized(_state) {
            _state = State.INITIALIZING;
            System.out.printf("~~~~ TestCtrl context state: %s ~~~~\n", _state.name());
        }

        // Load server configuration from WEB-INF/classes/testctrl/res/config.json
        loadConfig();
        
        // load the tests generator engine & initialize the tests database
        String questionsRoot = _servletContext.getRealPath("") + _config.tests_root;
        _generator = new Generator(questionsRoot);
        _testsDb = new TestsDb(questionsRoot);

        // load the web div engine
        String templatesRoot = _servletContext.getRealPath("");
        _webDiv = new WebDiv(templatesRoot);

        synchronized(_state) {
            _state = State.READY;
            System.out.printf("~~~~ TestCtrl context state: %s ~~~~\n", _state.name());
        }
        // TestCtrl ready to accept requests
    }

    private void runHeartbeat() {
        pruneSessions();
        checkWork();
    }
    // #endregion: [Private] Async runners

    // #region: [Public] Session management methods
    public Session newSession(User user, HttpSession httpSession, String rootUrl) throws NoSuchAlgorithmException {
        // check that either no session is active or the existing session belongs to the same user
        Session session = _sessions.get(httpSession);
        Servlet.checkTrue(session == null || session.getUser().equals(user), "Other user logged in this session!");

        // all good, if no pre-existing session for this user, create one
        if (session == null) {
            session = new Session(user, httpSession, rootUrl);
            _sessions.put(httpSession, session);
        }
        session.touch();
        return session;
    }

    public Session closeSession(HttpSession httpSession) {
        // remove the session from the _sessions map
        Session session = _sessions.remove(httpSession);
        Servlet.checkTrue(session != null, "Client session invalid.");
        return session;
    }

    public Session getSession(HttpSession httpSession) {
        return _sessions.get(httpSession);
    }
    
    public void pruneSessions() {
        _hbCounterPrunning = (_hbCounterPrunning + 1) % _HEARTBEATS_PRUNNING;
        if (_hbCounterPrunning != 0) {
            return;
        }

        synchronized(_state) {
            _state = State.CLEANING;
        }

        Map<HttpSession, Session> newSessions = new HashMap<HttpSession, Session>();
        Instant now = Instant.now();
        int activeSessions = 0;
        for(Map.Entry<HttpSession, Session> kvp : _sessions.entrySet()) {
            HttpSession httpSession = kvp.getKey();
            Session session = kvp.getValue();
            if (session.isOrphan(now)) {
                Log(new LogEntry("Session pruned > [%s] %s", session.getId(), session.getUser().username));
                continue;
            }
            newSessions.put(httpSession, session);
            activeSessions++;
        }
        if (_sessions.size() != activeSessions) {
            System.out.printf("TestCtrl sessions cleaned up ... [removed %d][remaining %d]\n", _sessions.size() - activeSessions, activeSessions);
            _sessions = newSessions;
        }

        synchronized(_state) {
            _state = State.READY;
        }
    }
    
    public void Log(LogEntry logEntry) {
        // Log the entry in all "admin" and "teacher" sessions
        for(Session session : _sessions.values()) {
            if (session.getUser().hasRole("admin", "teacher")) {
                session.Log(logEntry);
            }
        }
    }
    // #endregion: [Public] Session management methods

    // #region: [Public] Question-set methods
    public Generator getGenerator() {
        return _generator;
    }

    public TestsDb getTestsDb() {
        return _testsDb;
    }

    public WebDiv getWebDiv() {
        return _webDiv;
    }
    // #endregion: [Public] Question-set methods

    // #region: [Public] Async work methods
    public void QueueWork(Work work) {
        work.setContext(this);
        _queueWork.add(work);
    }

    public void checkWork() {
        _hbCounterWorking = (_hbCounterWorking + 1) % _HEARTBEATS_WORKING;
        if (_hbCounterWorking != 0) {
            return;
        }

        synchronized(_state) {
            _state = State.WORKING;
        }

        Work work = _queueWork.poll();
        if (work != null) {
            try {
                work.run();
            } catch (Exception e) {
                Log(new LogEntry("Work exception: '%s'", e.getMessage()));
            }
        }

        synchronized(_state) {
            _state = State.READY;
        }
    }
    // #endregion: [Public] Async work methods
}
