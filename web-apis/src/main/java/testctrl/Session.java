package testctrl;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class Session implements Comparable<Session> {
    // A Session which have not been touched for _LIFECHECK duration is considered orphaned and subjected to removal.
    private static final Duration _LIFECHECK = Duration.ofMinutes(5);

    private String _sessionId;
    private Instant _heartbeat;
    private Context.User _user;

    public Session(Context.User user) {
        _sessionId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        _user = user;
        _heartbeat = Instant.now();
    }

    public String getId() {
        return _sessionId;
    }

    public Context.User getUser() {
        return _user;
    }

    public void touch() {
        _heartbeat = Instant.now();
    }

    public boolean isOrphan(Instant now) {
        Duration lifetime = Duration.between(_heartbeat, now);
        return (lifetime.compareTo(_LIFECHECK) >= 0);
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
