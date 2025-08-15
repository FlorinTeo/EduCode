package testctrl;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;

public class Session implements Comparable<Session> {
    // A Session not touched for _IDLE_THRESHOLD duration is considered orphaned and subjected to removal.
    private static final Duration _IDLE_THRESHOLD = Duration.ofMinutes(5);

    private String _sessionId;
    private User _user;
    private HttpSession _httpSession;
    private Instant _heartbeat;

    public Session(User user, HttpSession httpSession) {
        _sessionId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        _user = user;
        _httpSession = httpSession;
        _heartbeat = Instant.now();
    }

    public String getId() {
        return _sessionId;
    }

    public User getUser() {
        return _user;
    }

    public HttpSession getHttpSession() {
        return _httpSession;
    }

    public void touch() {
        _heartbeat = Instant.now();
    }

    public boolean isOrphan(Instant now) {
        Duration lifetime = Duration.between(_heartbeat, now);
        return (lifetime.compareTo(_IDLE_THRESHOLD) >= 0);
    }

    @Override
    public int compareTo(Session o) {
        return _user.name.compareTo(o._user.name);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s : %s", _heartbeat.toString(), _sessionId, _user.name);
    }
}
