package testctrl.testmgmt;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestsDb {
    private String _dbPath;
    private Map<String, TMeta> _db;
    
    public TestsDb(String dbPath) {
        _dbPath = dbPath;
        _db = new HashMap<String, TMeta>();
        loadTests();
    }

    private void loadTests() {
        List<TMeta> tMetas = new LinkedList<TMeta>();

        // TODO: Scan file system for tests, deserialize and collect their metadata in tMetas

        synchronized(_db) {
            _db.clear();
            for(TMeta tMeta : tMetas) {
                _db.put(tMeta.getName(), tMeta);
            }
        }
    }

    public void refreshDb() {
        loadTests();
    }

    public Collection<THeader> getTRecs() {
        synchronized(_db) {
            List<THeader> tRecs = new LinkedList<THeader>();
            for(TMeta tMeta : _db.values()) {
                tRecs.add(new THeader(tMeta.getName()));
            }
            return tRecs;
        }
    }

    @Override
    public String toString() {
        return String.format("TestsDb: %d tests.", _db.size());
    }
}
