package testctrl;

import com.google.gson.Gson;

public class Answer {

    public class Msg extends Answer {
        public String _sid;
        public String _word;
        public String _message;


        public Msg(String sid, String message) {
            this(sid, message, null);
        }

        public Msg(String sid, String message, String word) {
            _sid = sid;
            _message = message;
            _word = word;
        }
    }

    public class Err extends Answer {
        public String _error;

        public Err(String error) {
            _error = error;
        }
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
