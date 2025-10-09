package testctrl;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;

import testctrl.testmgmt.QHeader;
import testctrl.testmgmt.THeader;
import testctrl.testmgmt.TMeta;

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

    public class QList extends Answer {
        public QHeader[] _qList;

        public QList(Collection<QHeader> qRecs) {
            _qList = qRecs.toArray(new QHeader[0]);
        }
    }

    public class TList extends Answer {
        public THeader[] _tList;

        public TList(Collection<THeader> tRecs) {
            _tList = tRecs.toArray(new THeader[0]);
        }
    }

    public class QData extends Answer {
        public QHeader _qHeader;
        public String _qDiv;

        public QData(QHeader qHeader, String qDiv) {
            _qHeader = qHeader;
            _qDiv = qDiv;
        }
    }

    public class TData extends Answer {
        public THeader _tHeader;
        public QHeader[] _qHeaders;
        public THeader[] _variants;

        public TData(TMeta tMeta, String testPath) {
            _tHeader = tMeta.getTHeader();
            _tHeader.adjustPath(testPath);
            _qHeaders = tMeta.getQHeaders().toArray(new QHeader[0]);
            ArrayList<THeader> variants = new ArrayList<THeader>();
            for(TMeta tVarMeta: tMeta.getVariants().values()) {
                THeader vtHeader = tVarMeta.getTHeader();
                vtHeader.adjustPath(String.format("%s/%s", testPath, tVarMeta.getVersion()));
                variants.add(vtHeader);
            }
            _variants = variants.toArray(new THeader[0]);
        }
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
