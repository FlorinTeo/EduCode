package testctrl.testmgmt;

import java.util.HashMap;
import java.util.Map;

public class THeader {
    public String _tName;
    public String _tVersion;
    public Map<String, String> _links;

    public THeader(String name, String version, Map<String,String> files) {
        _tName = name;
        _tVersion = version;
        _links = new HashMap<String, String>(files);
    }

    public void adjustPath(String pathPrefix) {
        Map<String, String> newLinks = new HashMap<String,String>();
        for (Map.Entry<String, String> kvp : _links.entrySet()) {
            newLinks.put(kvp.getKey(), String.format("%s/%s", pathPrefix, kvp.getValue()));
        }
        _links = newLinks;
    }
}
