package testctrl.testmgmt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TestsDb {
    private Path _pRoot;
    private Map<String, TMeta> _db;
    
    public TestsDb(String root) throws IOException {
        _pRoot = Paths.get(root);
        _db = new HashMap<String, TMeta>();
        loadTests();
    }

    private int loadTests() throws IOException {
        // scan for the top folders in _pRoot, excluding system and ".template" dirs.
        Set<String> excludedFolders = Set.of(".", "..", ".template");

        List<TMeta> tMetas = new LinkedList<TMeta>();
        List<Path> pTests = Files.list(_pRoot)
            .filter(Files::isDirectory)
            .filter(path -> !excludedFolders.contains(path.getFileName().toString()))
            .collect(Collectors.toList());
        for(Path pTest : pTests) {
            tMetas.add(new TMeta(pTest));
        }

        synchronized(_db) {
            _db.clear();
            for(TMeta tMeta : tMetas) {
                _db.put(tMeta.getName(), tMeta);
            }
            return _db.size();
        }
    }

    public int refreshDb() throws IOException {
        return loadTests();
    }

    public Collection<THeader> getTHeaders() {
        synchronized(_db) {
            List<THeader> tRecs = new LinkedList<THeader>();
            for(TMeta tMeta : _db.values()) {
                tRecs.add(tMeta.getTHeader());
            }
            return tRecs;
        }
    }

    public TMeta getTMeta(String tID) {
        synchronized(_db) {
            return _db.get(tID);
        }
    }

    @Override
    public String toString() {
        return String.format("TestsDb: %d tests.", _db.size());
    }
}
