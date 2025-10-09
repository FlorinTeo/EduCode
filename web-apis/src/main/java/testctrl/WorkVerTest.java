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

            // generate and log each of the variants
            String log = String.format("Test generated - <b>%s</b>: ref", tMeta.getName());
            for(String variant : Generator.VARIANTS) {
                TMeta vMeta = tMeta.getVariants().get(variant);
                log += String.format(",%s", vMeta.getVersion());
            }
            _context.Log(new LogEntry(log));
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
