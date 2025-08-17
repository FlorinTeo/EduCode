package testctrl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LogEntry {
        private static final DateTimeFormatter _LOG_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd-HH:mm:ss.SSS");
        public String _logTime;
        public String _logText;
        
        public LogEntry(String logFormat, Object... logArgs) {
            _logTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).format(_LOG_TIME_FORMATTER);
            _logText = String.format(logFormat, logArgs);
        }

    }
