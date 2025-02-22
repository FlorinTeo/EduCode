package edu.ftdev.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

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

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFactory;
import javafx.scene.effect.Light.Point;

public class MapCanvas extends DrawingFactory {

    // #region: [private] Internal class definitions
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
    // #endregion: [private] Internal class definitions

    // #region: [private] Internal fields
    // Map metadata (i.e {_name, _mapOverlaysRaw, _centerTL, _centerBR})
    private MapMetadata _mapMetadata;
    // Map<overlay_name, overlay_image> (i.e. {<"AB", imageAB>, <"AC", imageAC>, ..})
    private HashMap<String, BufferedImage> _mapOverlays;
    // Set<route_name> - Overlays currently rendered on map (i.e. {"AB", "CD", "BA", ...})
    private Set<String> _overlays = new HashSet<String>();
    // Map<route_node, routes_from_node> (i.e. {<"A", RouteNodeInfo(["AB", "AC"])>, <"B", RouteNodeInfo(["BA", "BC"])>, ..})
    private HashMap<Character, RouteNodeInfo> _routeInfoMap = new HashMap<Character, RouteNodeInfo>();    
    // #endregion: [private] Internal fields
    
    // #region: [private] Routes manual display
    private void buildRouteInfoMap() {
        // Set<String> routes = _mapImage.getRoutes();
        // for(String routeName : routes) {
        //     char key = Character.toUpperCase(routeName.charAt(0));
        //     RouteNodeInfo info = null;
        //     if (_routeInfoMap.containsKey(key)) {
        //         info = _routeInfoMap.get(key);
        //     } else {
        //         info = new RouteNodeInfo();
        //         _routeInfoMap.put(key, info);
        //     }
        //     info._routes.add(routeName);
        // }
    }
    
    private void showRoutes() {
        // List<String> routes = new ArrayList<String>();
        // for(RouteNodeInfo rni : _routeInfoMap.values()) {
        //     if (rni._index >= 0) {
        //         routes.add(rni._routes.get(rni._index));
        //     }
        // }
        // _mapImage.setOverlays(routes);
        // repaint();
    }
    // #endregion [private]: Routes manual display

    // #region: [private] Static helper methods    
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
    // #endregion: [private] Static IO helper methods

    // #region: [private] File IO
    private void loadFromFile(File file) throws IOException {
        Path filePath = Paths.get(file.getAbsolutePath());
        byte[] rawBytes = Files.readAllBytes(filePath);
        byte[] rawOffset = Arrays.copyOfRange(rawBytes, rawBytes.length-4, rawBytes.length);
        BigInteger offset = new BigInteger(rawOffset);
        byte[] rawJsonBytes = Arrays.copyOfRange(rawBytes,  offset.intValue(), rawBytes.length - 4);
        String rawjson = new String(rawJsonBytes);
        Gson deserializer = new Gson();
        _mapMetadata = deserializer.fromJson(rawjson, MapMetadata.class);
        byte[] rawImageBytes = Arrays.copyOfRange(rawBytes, 0, offset.intValue());
        InputStream imageStream = new ByteArrayInputStream(rawImageBytes);
        BufferedImage image = ImageIO.read(imageStream);
        _drawing = new Drawing(image);

        for(Map.Entry<String, String> mapOverlayRaw : _mapMetadata._mapOverlaysRaw.entrySet())
        {
            _mapOverlays.put(
                    mapOverlayRaw.getKey(), 
                    base64ToImage(mapOverlayRaw.getValue()));
        }
    }
    
    private void loadFromDir(File dir) throws IOException {
        // Create the map metadata object
        _mapMetadata = new MapMetadata();
        // Load the baseMap and create the mapImage
        _mapMetadata._mapName = dir.getName();
        File mapFile = new File(dir.getName() + "/" + _mapMetadata._mapName + "_.jpg");
        _drawing = new Drawing(ImageIO.read(mapFile));
        
        // Load the overlays into the mapImage
        FilenameFilter overlayFilter = (file, name)-> { return name.matches(dir + "_.+\\.png"); };
        for (String overlayFileName : dir.list(overlayFilter)) {
            File overlayFile = new File(dir.getName() + "/" + overlayFileName);
            String overlayName = overlayFileName.split("_|\\.")[1];
            _mapOverlays.put(
                    overlayName,
                    ImageIO.read(overlayFile));
        }
    }
    // #endregion: [private] File IO

    public MapCanvas() {
        super();
    }
    
}
