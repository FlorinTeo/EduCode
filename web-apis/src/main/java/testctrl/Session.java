package testctrl;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;

public class Session implements Comparable<Session> {
    // A Session not touched for _IDLE_THRESHOLD duration is considered orphaned and subjected to removal.
    private static final Duration _IDLE_THRESHOLD = Duration.ofMinutes(5);
    private static final int _MAX_LOG_ENTRIES = 100;

    private String _sessionId;
    private User _user;
    private HttpSession _httpSession;
    private Instant _heartbeat;
    private Queue<LogEntry> _logEntries;

    public Session(User user, HttpSession httpSession) {
        _sessionId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        _user = user;
        _httpSession = httpSession;
        _heartbeat = Instant.now();
        _logEntries = new LinkedList<LogEntry>();
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

    public void Log(LogEntry logEntry) {
        _logEntries.add(logEntry);
        if (_logEntries.size() > _MAX_LOG_ENTRIES) {
            _logEntries.poll(); // Remove the oldest log entry if limit exceeded
        }
    }

    public LogEntry[] purgeLog() {
        LogEntry[] entries = _logEntries.toArray(new LogEntry[0]);
        _logEntries.clear();
        return entries;
    }

    @Override
    public int compareTo(Session o) {
        return _user.compareTo(o._user);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s : %s", _heartbeat.toString(), _sessionId, _user.username);
    }
}
