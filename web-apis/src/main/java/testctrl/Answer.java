package testctrl;

import com.google.gson.Gson;

public class Answer {

    public class Msg extends Answer {
        public String _sid;
        public String _message;

        public Msg(String sid, String format, Object... args) {
            _sid = sid;
            _message = String.format(format, args);
        }
    }

    public class Err extends Answer {
        public String _error;

        public Err(String format, Object... args) {
            _error = String.format(format, args);
        }
    }

    public class Logs extends Answer {
        public LogEntry[] _logs;

        public Logs(LogEntry... logs) {
            _logs = logs;
        }
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
