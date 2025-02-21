package edu.ftdev.Map;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.ftdev.DrawingFrame;
import edu.ftdev.KeyInterceptor;

/**
 * Class provide GUI functionality for interacting with a MapImage.
 * Allows the user to pan and zoom over the map, overlay
 * the embedded routes and control the execution flow. 
 * @see MapImage
 * @see #MapFrame(MapImage)
 * @see #step()
 * @see #leap()
 * @see #jump()
 * @see #setStatusMessage(String)
 */
public class MapFrame extends DrawingFrame {

    private MapImage _mapImage;

    // #region: [private] Routes manual display
    private class RouteNodeInfo {
        private int _index;
        private ArrayList<String> _routes; 
        
        public RouteNodeInfo() {
            _index = -1;
            _routes = new ArrayList<String>();
        }
    }

    private HashMap<Character, RouteNodeInfo> _routeInfoMap = new HashMap<Character, RouteNodeInfo>();
    
    private void buildRouteInfoMap() {
        Set<String> routes = _mapImage.getRoutes();
        for(String routeName : routes) {
            char key = Character.toUpperCase(routeName.charAt(0));
            RouteNodeInfo info = null;
            if (_routeInfoMap.containsKey(key)) {
                info = _routeInfoMap.get(key);
            } else {
                info = new RouteNodeInfo();
                _routeInfoMap.put(key, info);
            }
            info._routes.add(routeName);
        }
    }
    
    private void showRoutes() {
        List<String> routes = new ArrayList<String>();
        for(RouteNodeInfo rni : _routeInfoMap.values()) {
            if (rni._index >= 0) {
                routes.add(rni._routes.get(rni._index));
            }
        }
        _mapImage.setOverlays(routes);
        repaint();
    }
    // #endregion [private]: Routes manual display

    // #region: [private] KeyInterceptor hooks
    private KeyInterceptor.KeyHook _onKeyABCDE = (keyEvent) -> {
        char key = Character.toUpperCase(keyEvent.getKeyChar());
        if (!_routeInfoMap.containsKey(key)) {
            return;
        }
        RouteNodeInfo oInfo = _routeInfoMap.get(key);
        oInfo._index++;
        if (oInfo._index == oInfo._routes.size()) {
            oInfo._index = -1;
        }
        showRoutes();
    };
    
    private KeyInterceptor.KeyHook _onKeyDelete = (keyEvent) -> {
        for(RouteNodeInfo oi : _routeInfoMap.values()) {
            oi._index = -1;
        }
        showRoutes();
    };
    
    private KeyInterceptor.KeyHook _onKeyX = (keyEvent) -> {
        Set<String> routes = _mapImage.getOverlays();
        if (routes.size() == 0) {
            this.setStatusMessage("?");
        } else {
            boolean collide = _mapImage.collide(routes.stream().toArray(String[] ::new));
            this.setStatusMessage(collide ? "collide" : "clear");            
        }
    };
    // #endregion: [private] KeyInterceptor hooks

    /**
     * Constructs a new MapFrame and loads it with a given MapImage.
     * @param mapImage - MapImage object to be loaded in the frame
     * @throws IOException resources needed to display the MapFrame,
     * such as bitmaps for the control buttons cannot be loaded from the disk.
     */
    public MapFrame(MapImage mapImage) throws IOException {
        super(mapImage);
        
        // adjust window title
        setTitle("Map Framework GUI");
        
        _mapImage = mapImage;
        buildRouteInfoMap();
        
        // hook in the key intercepts
        setKeyTypedHook('A', _onKeyABCDE);
        setKeyTypedHook('B', _onKeyABCDE);
        setKeyTypedHook('C', _onKeyABCDE);
        setKeyTypedHook('D', _onKeyABCDE);
        setKeyTypedHook('E', _onKeyABCDE);
        setKeyTypedHook('X', _onKeyX);
        setKeyTypedHook(KeyEvent.VK_DELETE, _onKeyDelete);
    }
}
