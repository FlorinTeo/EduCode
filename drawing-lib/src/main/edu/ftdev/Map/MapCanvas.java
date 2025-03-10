package edu.ftdev.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFactory;
import edu.ftdev.DrawingFrame;
import edu.ftdev.KeyInterceptor;
import edu.ftdev.KeyInterceptor.KeyHook;

/**
 * MapCanvas provides the abstractions and the user interface for displaying and interacting with a street intersection on a road map.
 * The roads converging in an intersection are labeled with a letter: A, B, C, D, etc. The routes connecting these roads represent
 * the various ways the intersection can be travelled through.
 * <p>
 * Map images embed the base map and the information about the routes, specific to that map. The image below depicts two routes, AC and
 * CD overlaid on the map. There are others such routes which are not overlaid: AD, ED, etc. Note, depending on the map, some routes
 * may not be possible: for example no routes can originate at point B since B sits on a one-way road, leaving away from the intersection.
 * </p>
 * <p>
 * <img src="https://florinteo.github.io/EduCode/DrawingLib/res/Map/map_canvas-collide.png" alt="maze_canvas-collide.png" style="height: 320px;">
 * </p>
 * <p>
 * A MazeCanvas instance, created for given map image can be used to fetch all the available routes in that image. The routes can be overlaid
 * in any combination and same instance can provide collision information such as AC and CD colliding in the intersection. MazeCanvas also
 * provides the ability to enable interactive exploration of the routes by registering specific key strokes with the custom code.
 * </p>
 * The following code demonstrates the usage of the MapCanvas class and the way to interact with the routes by means of key hooks:
 * <pre>{@code
 * MapCanvas mp = new MapCanvas("Woodlawn.jpg");
 * mp.open();
 * Queue<String> routes = new LinkedList<String>(mp.getRoutes());
 * mp.setKeyHook(KeyEvent.VK_TAB, onTab, mp, routes);
 * mp.breakLeap();
 * mp.close();
 * } </pre>
 * In this snippet of code, <i>onTab</i> is a lambda function that will be called whenever the TAB key is pressed. The lambda function
 * will cycle through the routes and overlay them on the map. The <i>mp</i> object is the MapCanvas instance that will be used to interact
 * with the map image. Both the <i>mp</i> object and the <i>routes</i> queue are passed as arguments to the lambda function. The lambda
 * function definition is as follows:
 * <pre>{@code
 *  private KeyHook onTab = (keyEvent, args) -> {
 *     MapCanvas mp = (MapCanvas) args[0];
 *     Queue<String> routes = (Queue<String>)args[1];
 *     mp.setOverlays(routes.peek());
 *     routes.add(routes.remove());
 * };
 * }</pre>
 * @see KeyInterceptor
 */
public class MapCanvas extends DrawingFactory {

    // #region: [Private] Internal class definitions
    /**
     * Private class used for serialization/deserialization of the
     * metadata associated with an enhanced map image (.jpeg) file.
     */
    private class MapMetadata {
        private String _mapName = "";
        private HashMap<String, String> _mapOverlaysRaw = new HashMap<String, String>();
        private Point _centerTL;
        private Point _centerBR;
    };

    /**
     * Private class used to store the information about the routes
     * that can be overlaid on the map image.
     */
    private class RouteNodeInfo {
        private int _index;
        private ArrayList<String> _routes; 
        
        public RouteNodeInfo() {
            _index = -1;
            _routes = new ArrayList<String>();
        }
    }
    // #endregion: [Private] Internal class definitions

    // #region: [Private] Internal fields
    // Map metadata (i.e {_name, _mapOverlaysRaw, _centerTL, _centerBR})
    private MapMetadata _mapMetadata;
    // Map<overlay_name, overlay_image> (i.e. {<"AB", imageAB>, <"AC", imageAC>, ..})
    private HashMap<String, BufferedImage> _mapOverlays = new HashMap<String, BufferedImage>();
    // Set<route_name> - Set of overlay routes rendered on map (i.e. {"AB", "CD", "BA", ...})
    private Set<String> _overlays = new HashSet<String>();
    // Map<route_node, routes_from_node> (i.e. {<"A", RouteNodeInfo(["AB", "AC"])>, <"B", RouteNodeInfo(["BA", "BC"])>, ..})
    private HashMap<Character, RouteNodeInfo> _routeInfoMap = new HashMap<Character, RouteNodeInfo>();    
    // #endregion: [Private] Internal fields
    
    // #region: [Private] Routes display on keyboard input
    private void buildRouteInfoMap() {
        Set<String> routes = getRoutes();
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
        setOverlays(routes);
        repaint();
    }
    // #endregion [Private]: Routes display on keyboard input

    // #region: [Private] Static helper methods    
    private static String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }
    
    private static BufferedImage base64ToImage(String base64) throws IOException {
        byte[] imgBytes = Base64.getDecoder().decode(base64);
        InputStream imgStream = new ByteArrayInputStream(imgBytes);
        BufferedImage image = ImageIO.read(imgStream);
        return image;
    }
    
    private static byte[] toByteArray(BigInteger big, int minLength) {
        byte[] base=big.toByteArray();
        byte[] returnArray=new byte[Math.max(base.length, minLength)];
        if ((base[0]&128)!=0) {
            Arrays.fill(returnArray, (byte) 0xFF);
        }
        System.arraycopy(base,0,returnArray,returnArray.length-base.length,base.length);
        return returnArray;
    }
    // #endregion: [Private] Static IO helper methods

    // #region: [Private] Instance helper methods
    private BufferedImage loadFromRawBytes(byte[] rawBytes) throws IOException {
        byte[] rawOffset = Arrays.copyOfRange(rawBytes, rawBytes.length-4, rawBytes.length);
        BigInteger offset = new BigInteger(rawOffset);
        byte[] rawJsonBytes = Arrays.copyOfRange(rawBytes,  offset.intValue(), rawBytes.length - 4);
        String rawjson = new String(rawJsonBytes);
        Gson deserializer = new Gson();
        _mapMetadata = deserializer.fromJson(rawjson, MapMetadata.class);
        byte[] rawImageBytes = Arrays.copyOfRange(rawBytes, 0, offset.intValue());

        for(Map.Entry<String, String> mapOverlayRaw : _mapMetadata._mapOverlaysRaw.entrySet())
        {
            _mapOverlays.put(
                    mapOverlayRaw.getKey(), 
                    base64ToImage(mapOverlayRaw.getValue()));
        }

        InputStream imageStream = new ByteArrayInputStream(rawImageBytes);
        BufferedImage image = ImageIO.read(imageStream);
        return image;
    }
    
    private BufferedImage loadFromDir(File dir) throws IOException {
        // Create the map metadata object
        _mapMetadata = new MapMetadata();
        // Load the baseMap and create the mapImage
        _mapMetadata._mapName = dir.getName();
       
        // Load the overlays into the _mapOverlays and in the _mapMetadata._mapOverlaysRaw maps
        FilenameFilter overlayFilter = (file, name)-> { return name.matches(dir + "_.+\\.png"); };
        for (String overlayFileName : dir.list(overlayFilter)) {
            File overlayFile = new File(dir.getName() + "/" + overlayFileName);
            String overlayName = overlayFileName.split("_|\\.")[1];
            BufferedImage overlayImage = ImageIO.read(overlayFile);
            _mapOverlays.put(overlayName, overlayImage);
            _mapMetadata._mapOverlaysRaw.put(overlayName, imageToBase64(overlayImage));
        }

        File mapFile = new File(dir.getName() + "/" + _mapMetadata._mapName + "_.jpg");
        return ImageIO.read(mapFile);
    }

    /**
     * Saves the content of this MapImage object into an enhanced .jpeg file.
     * The resulting .jpeg file is an image of the base map followed by a 
     * JSON serialized object containing the routes overlays.
     * @param mapImageFileName - the name of the .jpg file to be created.<br>
     * e.g.: "Ravenna.jpg"
     * @throws IOException - failure in writing to the disk.
     * @see #load(String)
     */
    @SuppressWarnings("unused") // To be used only by future internal tooling for genrating map images.
    private void save(String mapImageFileName) throws IOException {
        Gson serializer = new Gson();
        String jsonMapRoutes = serializer.toJson(_mapMetadata);
        Path mapImagePath = Paths.get(mapImageFileName);
        ByteArrayOutputStream mapImageStream = new ByteArrayOutputStream();
        ImageIO.write(_drawing.getImage(), "jpg", mapImageStream);
        byte[] mapImageBytes = mapImageStream.toByteArray();
        Files.write(mapImagePath, mapImageBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        byte[] mapRoutesBytes = jsonMapRoutes.getBytes();
        Files.write(mapImagePath, mapRoutesBytes, StandardOpenOption.APPEND);
        byte[] mapImageLenBytes = toByteArray(BigInteger.valueOf(mapImageBytes.length), 4);
        Files.write(mapImagePath, mapImageLenBytes, StandardOpenOption.APPEND);
    }

    private void refresh() {
        _drawing.reset();
        Graphics2D g=_drawing.getGraphics();
        for (String overlay : _overlays) {
            if (_mapOverlays.containsKey(overlay)) {
                g.drawImage(_mapOverlays.get(overlay),0,0,null);
            }
        }
        _drawingFrame.repaint();
    }
    // #endregion: [Private] instance helper methods

    /**
     * Constructs a new MapCanvas object from a map image. The map image can be a .jpg file,
     * a directory containing the map image and its overlays, or a resource path. 
     * @param mapImagePath path to the image file, directory or resource.
     * @throws IOException if the map image cannot be located at the given path.
     */
    public MapCanvas(String mapImagePath) throws IOException {
        File mapImageFile = new File(mapImagePath);
        if (mapImageFile.exists() && mapImageFile.isDirectory()) {
        }
        if (mapImageFile.exists()) {
            // load map from the disk
            if (mapImageFile.isDirectory()) {
                // load map from base & overlay images in a folder
                BufferedImage image = loadFromDir(mapImageFile);
                _drawing = new Drawing(image);
            } else {
                // load from enhanced image file
                byte[] rawBytes = bytesFromFile(mapImageFile);
                BufferedImage image = loadFromRawBytes(rawBytes);
                _drawing = new Drawing(image);
            }
        } else {
            // load map from enhanced image file from the package resources
            byte[] rawBytes = bytesFromRes(mapImagePath);
            BufferedImage image = loadFromRawBytes(rawBytes);
            _drawing = new Drawing(image);
        }
        _drawingFrame = new DrawingFrame(_drawing);
        buildRouteInfoMap();
    }

    // #region: [Public] MapCanvas API
    /**
     * Gets the name of the map in this MapImage.
     * @return The name of the map.
     */
    public String getMapName() {
        return _mapMetadata._mapName;
    }
    
    /**
     * Gets the set of all the routes, given by name, embedded in this map.
     * @return The names of all the routes embedded with this map.<br>
     * e.g.: {"AB", "AC", "AD", "BA", "BC", "BD", ...}
     */
    public Set<String> getRoutes() {
        return _mapOverlays.keySet();
    }
    
    /**
     * Gets the set of routes overlaid on the map. This is a subset
     * of all the routes embedded in this map.
     * @return The names of the routes overlayed on the map.<br>
     * e.g.: {"AB", "AC", "CB", ...}
     * @see #setOverlays(String...)
     * @see #getRoutes()
     */
    public Set<String> getOverlays() {
        return new TreeSet<String>(_overlays);
    }
    
    /**
     * Sets the routes to be overlaid on the map. This is expected
     * to be a subset of all the routes embedded in this map.
     * @param routes var arg array with the routes to be overlaid on the map.
     * @see #getOverlays()
     * @see #getRoutes()
     */
    public void setOverlays(String... routes) {
        setOverlays(Arrays.asList(routes));
    }
    
    /**
     * Sets the routes to be overlaid on the map. This is expected
     * to be a subset of all the routes embedded in this map.
     * @param routes collection (List, or Set) with the routes to be overlaid on the map.
     * @see #getOverlays()
     * @see #getRoutes()
     */
    public void setOverlays(Collection<String> routes) {
        _overlays.clear();
        _overlays.addAll(routes);
        refresh();
    }
    
    /**
     * Indicates whether any of the given routes are colliding with any other.
     * A collision is detected if any of the route overlays have non-transparent
     * pixels of different colors at the same coordinates on the map <br>
     * <p><u>Examples:</u><br>
     * assuming "AB" and "AC" have same color, collide("AB", "AC")
     * returns false<br>
     * assuming "AB" and "CA" have different colors and have overlapping pixels,
     * collide("AB","CA") returns true. 
     * @param routes the list of route names to be tested.
     * @return True if the routes do not collide, false otherwise.
     */
    public boolean collide(String... routes) {
        int xMin = 0;
        int yMin = 0;
        if (_mapMetadata._centerTL != null) {
            xMin = (int)_mapMetadata._centerTL.getX();
            yMin = (int)_mapMetadata._centerTL.getY();
        }
        int xMax = _drawing.getWidth();
        int yMax = _drawing.getHeight();
        if (_mapMetadata._centerBR != null) {
            xMax = (int)_mapMetadata._centerBR.getX()+1;
            yMax = (int)_mapMetadata._centerBR.getY()+1;
        }
        for (int x = xMin; x < xMax; x++) {
            for (int y = yMin; y < yMax; y++) {
                String lastOpaque = null;
                for(String route : routes) {
                    if (!_mapOverlays.containsKey(route)) {
                        continue;
                    }
                    
                    int overlayPix = _mapOverlays.get(route).getRGB(x, y);
                    if ((overlayPix >> 24) != 0) {
                        if (lastOpaque == null) {
                            lastOpaque = route;
                        }
                        if (route.charAt(0) != lastOpaque.charAt(0)) {
                            return true;
                        }
                    }
                }
            }
        }
       
        return false;
    }
    // #endregion: [Public] MapCanvas API

    // #region: [Public] Key hooking methods and lambdas
    /**
     * Registers a key hook for the given key. The key hook is a lambda
     * function that will be called whenever the key is typed.
     * @param key the key to be hooked.
     * @param hook the lambda function to be called when the key is pressed.
     * @param args the arguments to be passed to the hook when the key is pressed.
     */
    public void setKeyHook(int key, KeyHook hook, Object... args) {
        _drawingFrame.setKeyTypedHook(key, hook, args);
    }

    /**
     * Registers a set of demo key hooks. When enabled, typing the keys
     * corresponding to the route endpoints will cycle through the routes originating
     * at that endpoint and overlay them on the map. Typing 'X' performs a collision test on the overlayed routes.
     * @param enable true to enable the demo key hooks, false to disable.
     */
    public void setDemoKeyHooks(boolean enable) {
        for(char key : _routeInfoMap.keySet()) {
            _drawingFrame.setKeyTypedHook(key, enable ? _onDemoKeyHook : null);
        }
        _drawingFrame.setKeyTypedHook('X', enable ? _onDemoKeyHook : null);
    }
    
    private KeyInterceptor.KeyHook _onDemoKeyHook = (keyEvent, args) -> {
        char key = Character.toUpperCase(keyEvent.getKeyChar());
        
        // if the key is 'X', perform a collision test on the overlayed routes
        if (key == 'X') {
            boolean collides = collide(_overlays.toArray(new String[0]));
            _drawingFrame.setStatusMessage(collides ? "Collide" : "Not collide");
            return;
        }

        // if the key is not matching a rounte endpoint, do nothing (return).
        if (!_routeInfoMap.containsKey(key)) {
            return;
        }

        // cycle through the routes originating at the key endpoint
        RouteNodeInfo oInfo = _routeInfoMap.get(key);
        oInfo._index++;
        if (oInfo._index == oInfo._routes.size()) {
            oInfo._index = -1;
        }
        showRoutes();
    };
    // #endregion: [Public] Key hooking API
}
