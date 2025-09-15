package testctrl;

public abstract class Work {
    protected Session _session;
    protected Context _context;
    public Work(Session session) {
        _session = session;
        _context = null;
    }

    public void setContext(Context context) {
        _context = context;
    }

    public Session getSession() {
        return _session;
    }

    public abstract void run() throws Exception;
}
