package testctrl;

import java.io.IOException;

import testctrl.testmgmt.Generator;

public class WorkVerTest extends Work {
    private String _testName;
    private String[] _testQIDs;

    public WorkVerTest(Session session, String testName, String[] testQIDs) {
        super(session);
        _testName = testName;
        _testQIDs = testQIDs;
    }

    public void run() throws IOException {
        _context.Log(new LogEntry("Dequeing work: %s", toString()));
        Generator g = _context.getGenerator();

        if (_testQIDs.length == 0) {
            g.delTest(_testName);
            _context.Log(new LogEntry("Test '%s' deleted successfully.", _testName));
        } else {
            String[] htmlFiles = g.genTest(_testName, _testQIDs, true);
            String testUrl = String.format("%s/%s/%s", _session.getRootUrl(), _context.getConfig().tests_root, _testName);
            String log = "Reference test generated - ";
            log += String.format("<b>%s</b>:[<a href='%s/%s' target='blank'>test</a>, <a href='%s/%s' target='blank'>answers</a>].",
                _testName,
                testUrl, htmlFiles[0],
                testUrl, htmlFiles[1]);
            _context.Log(new LogEntry(log));
        }
    }

    @Override
    public String toString() {
        return String.format("WorkVerTest(%s)", _testName);
    }
}
