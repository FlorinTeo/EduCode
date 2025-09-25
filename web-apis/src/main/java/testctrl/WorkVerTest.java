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
            g.delTest(_testName, false);
            _context.Log(new LogEntry("Test '%s' deleted successfully.", _testName));
        } else {
            g.delTest(_testName, true);
            String[] htmlFiles = g.genTest(_testName, _testQIDs, true);
            String testUrl = String.format("%s/%s/%s", _session.getRootUrl(), _context.getConfig().tests_root, _testName);
            String log = "Reference test generated - ";
            log += String.format("<b>%s</b>:[<a href='%s/%s' target='blank'>test</a>, <a href='%s/%s' target='blank'>answers</a>].",
                _testName,
                testUrl, htmlFiles[0],
                testUrl, htmlFiles[1]);
            _context.Log(new LogEntry(log));

            String[] variants = { "v1", "v2", "v3", "v4" };
            int frqIndex = 0;
            for(String variant : variants) {
                htmlFiles = g.genTestVariant(_testName, variant, frqIndex++);
                testUrl = String.format("%s/%s/%s", _session.getRootUrl(), _context.getConfig().tests_root, _testName);
                log = "Variant test generated - ";
                log += String.format("<b>%s</b>:[<a href='%s/%s/%s' target='blank'>test</a>, <a href='%s/%s/%s' target='blank'>answers</a>].",
                    _testName + "." + variant,
                    testUrl, variant, htmlFiles[0],
                    testUrl, variant, htmlFiles[1]);
                _context.Log(new LogEntry(log));
            }
        }

        // refresh tests database
        _context.getTestsDb().refreshDb();
        _context.Log(new LogEntry("Tests database updated."));
    }

    @Override
    public String toString() {
        return String.format("WorkVerTest(%s)", _testName);
    }
}
