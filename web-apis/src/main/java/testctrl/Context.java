package testctrl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import testctrl.testmgmt.Generator;

public class Context extends TimerTask {
    private final static int _DELAY_START = 8; // trigger servlet initialization asynchronously, with 8ms delay
    private final static int _HEARTBEAT_INTERVAL = 10000; // 10sec cycle for heartbeat activities  (i.e. sessions cleanup)

    // #region: [Public] Enum & Class definitions pertaining to TestCtrl context.
    public enum State {
        INITIALIZING,
        READY,
        CLEANING,
        CLOSING,
        STOPPED
    }

    public class Config {
        public String tests_root;
        public List<User> users;
    };
    // #endregion: [Public] Enum & Class definitions pertaining to TestCtrl context.

    // #region: [Private] Instance variables for TestCtrl context.
    private ServletContext _servletContext;
    private Config _config;
    // Map of sessions keyed by their session ID.
    private Map<HttpSession, Session> _sessions;
    private State _state;
    private Timer _timer;
    // Test management fields
    private Generator _generator;
    // #endregion: [Private] Instance variables for TestCtrl context.

    public Context(ServletContext servletContext) {
        _servletContext = servletContext;
        _config = null;
        _sessions = new HashMap<HttpSession, Session>();
        _state = State.INITIALIZING;
        _timer = new Timer();
        // in 8ms load the servlet, then every minute perform timer-based operations!
        _timer.schedule(this, _DELAY_START, _HEARTBEAT_INTERVAL);
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
    public Config loadConfig() throws IOException {
        String configPath = _servletContext.getRealPath("WEB-INF\\classes\\testctrl\\res\\config.json");
        String configString = Files.readString(Paths.get(configPath));
        Gson deserializer = new Gson();
        return deserializer.fromJson(configString, Config.class);
    }

    public Config getConfig() {
        return _config;
    }

    public boolean saveConfig() throws IOException {
        String configPath = _servletContext.getRealPath("WEB-INF\\classes\\testctrl\\res\\config.json");
        Gson serializer = new GsonBuilder().setPrettyPrinting().create();
        String configString = serializer.toJson(_config);
        Files.writeString(Paths.get(configPath), configString);
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
        _config = loadConfig();
        String rootPath = _servletContext.getRealPath("") + _config.tests_root;
        _generator = new Generator(rootPath);

        synchronized(_state) {
            _state = State.READY;
            System.out.printf("~~~~ TestCtrl context state: %s ~~~~\n", _state.name());
        }
        // TestCtrl ready to accept requests
    }

    private void runHeartbeat() {
        synchronized(_state) {
            _state = State.CLEANING;
            System.out.printf("~~~~ TestCtrl Context state: %s ~~~~\n", _state.name());
        }
        pruneSessions();
        synchronized(_state) {
            _state = State.READY;
            System.out.printf("~~~~ TestCtrl Context state: %s ~~~~\n", _state.name());
        }
    }
    // #endregion: [Private] Async runners

    // #region: [Public] Session management methods
    public Session newSession(User user, HttpSession httpSession) throws NoSuchAlgorithmException {
        // check that either no session is active or the existing session belongs to the same user
        Session session = _sessions.get(httpSession);
        Servlet.checkTrue(session == null || session.getUser().equals(user), "Other user logged in this session!");

        // all good, if no pre-existing session for this user, create one
        if (session == null) {
            session = new Session(user, httpSession);
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
    // //#endregion: [Public] Question-set methods
}
