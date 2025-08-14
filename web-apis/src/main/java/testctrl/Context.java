package testctrl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

import jakarta.servlet.ServletContext;

public class Context extends TimerTask {

    // #region: [Public] Enum & Class definitions pertaining to TestCtrl context.
    public enum State {
        INITIALIZING,
        READY,
        CLOSING,
        STOPPED
    }

    public class Config {
    };
    // #endregion: [Public] Enum & Class definitions pertaining to TestCtrl context.

    // #region: [Private] Instance variables for TestCtrl context.
    private ServletContext _servletContext;
    private Config _config;
    private State _state;
    private Timer _timer;
    // #endregion: [Private] Instance variables for TestCtrl context.

    public Context(ServletContext servletContext) {
        _servletContext = servletContext;
        _config = null;
        _state = State.INITIALIZING;
        _timer = new Timer();
        // in 8ms load the servlet, then every minute perform timer-based operations!
        _timer.schedule(this, 8, 60000);
    }

    public boolean isReady() {
        synchronized(_state) {
            return _state == State.READY;
        }
    }

    public State getState() {
        return _state;
    }

    public Config getConfig() {
        return _config;
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

    @Override
    public void run() {
        switch(_state) {
        case INITIALIZING:
                try {
                    runInitialize();
                } catch (IOException e) {
                    System.out.printf("[EXC TestCtrl]:  initialization failure: %s\n", e.getMessage());
                    e.printStackTrace();
                }
            break;
        case READY:
            // timer-based operations while the server is ready
            break;
        default:
            // no action on any of the other states!
            break;
        }
    }

    public void runInitialize() throws IOException {
        synchronized(_state) {
            _state = State.INITIALIZING;
            System.out.printf("~~~~ TestCtrl context state: %s ~~~~\n", _state.name());
        }

        // Load server configuration from WEB-INF/classes/testctrl/res/config.json
        String configPath = _servletContext.getRealPath("WEB-INF\\classes\\testctrl\\res\\config.json");
        String configString = Files.readString(Paths.get(configPath));
        Gson deserializer = new Gson();
        _config = deserializer.fromJson(configString, Config.class);

        synchronized(_state) {
            _state = State.READY;
            System.out.printf("~~~~ TestCtrl context state: %s ~~~~\n", _state.name());
        }

        // TestCtrl ready to accept requests
    }
}
