package testctrl;

import java.io.IOException;

import testctrl.testmgmt.Generator;
import testctrl.testmgmt.TMeta;

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
            // clean any previous instance of this test, then regenerate it.
            g.delTest(_testName, true);
            TMeta tMeta = g.genTest(_testName, _testQIDs, true);

            // generate and log the links for the reference test
            String testUrl = String.format("%s/%s/%s", _session.getRootUrl(), _context.getConfig().tests_root, _testName);
            String log = "Reference test generated - ";
            log += String.format("<b>%s</b>:[<a href='%s/%s' target='blank'>test</a>, <a href='%s/%s' target='blank'>answers</a>].",
                tMeta.getName(),
                testUrl, tMeta.getFile("test"),
                testUrl, tMeta.getFile("answers"));
            _context.Log(new LogEntry(log));

            // generate and log the links for each of the variants
            for(String variant : Generator.VARIANTS) {
                TMeta vMeta = tMeta.getVariants().get(variant);
                testUrl = String.format("%s/%s/%s", _session.getRootUrl(), _context.getConfig().tests_root, _testName);
                log = "Variant test generated - ";
                log += String.format("<b>%s</b>:[<a href='%s/%s/%s' target='blank'>test</a>, <a href='%s/%s/%s' target='blank'>answers</a>].",
                    vMeta.getName() + "." + vMeta.getVersion(),
                    testUrl, variant, vMeta.getFile("test"),
                    testUrl, variant, vMeta.getFile("answers"));
                _context.Log(new LogEntry(log));
            }
        }

        // refresh tests database
        int nTests = _context.getTestsDb().refreshDb();
        _context.Log(new LogEntry("Tests database updated [%d tests].", nTests));
    }

    @Override
    public String toString() {
        return String.format("WorkVerTest(%s)", _testName);
    }
}
