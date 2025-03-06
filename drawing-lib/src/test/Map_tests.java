import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

import edu.ftdev.KeyInterceptor.KeyHook;
import edu.ftdev.Map.MapCanvas;
import edu.ftdev.Map.MoonCanvas;

public class Map_tests {
    private static MapCanvas _mapCanvas;

    private static KeyHook _onKeyT = (keyEvent, args) -> {
        String statusText = "Key: '" + keyEvent.getKeyChar() + "'; ";
        statusText += "Routes: " + _mapCanvas.getRoutes();
        _mapCanvas.setStatusMessage(statusText);
    };

    @Test
    public void routesTest() throws IOException {
        // loads an intersection image file and displays it in a map frame.
        _mapCanvas = new MapCanvas("Ravenna.jpg");
        // registers the key T with the method _onKeyT
        _mapCanvas.setKeyHook('T', _onKeyT);
        // opens the GUI window
        _mapCanvas.open();
        _mapCanvas.setStatusMessage("Press T to display the routes");
        // stops, waiting for user action
        _mapCanvas.breakJump();
        // close the window and terminate the program
        _mapCanvas.close();
    }

    @Test
    public void collisionTest() throws IOException {
        // opens the GUI window
        _mapCanvas = new MapCanvas("Woodlawn.jpg");
        _mapCanvas.open();
        _mapCanvas.breakStep();
        // sets the overlays to the routes "AB" and "CD" and checks if they collide. They should not.
        _mapCanvas.setOverlays("BA", "CD");
        _mapCanvas.setStatusMessage(_mapCanvas.collide("BA", "CD") ? "Collide" : "Not collide");
        _mapCanvas.breakStep();
        // sets the overlays to the routes "AD" and "CE" and checks if they collide. They should.
        _mapCanvas.setOverlays("AD", "CE");
        _mapCanvas.setStatusMessage(_mapCanvas.collide("AD", "CE") ? "Collide" : "Not collide");
        _mapCanvas.breakJump();
        _mapCanvas.close();
    }

    @Test
    public void demoKeyHooksTest() throws IOException {
        _mapCanvas = new MapCanvas("Loyal.jpg");
        _mapCanvas.setOverlays();
        _mapCanvas.open();
        _mapCanvas.setStatusMessage("demo key hooks enabled");
        _mapCanvas.setDemoKeyHooks(true);
        _mapCanvas.breakJump();
        _mapCanvas.setStatusMessage("demo key hooks disabled");
        _mapCanvas.setDemoKeyHooks(false);
        _mapCanvas.breakJump();
        _mapCanvas.close();
    }

    @SuppressWarnings("unchecked")
    private KeyHook onTab = (keyEvent, args) -> {
        MapCanvas mp = (MapCanvas) args[0];
        Queue<String> routes = (Queue<String>)args[1];
        mp.setOverlays(routes.peek());
        routes.add(routes.remove());
    };

    @Test
    public void docCodeTest() throws IOException {
        MapCanvas mp = new MapCanvas("Woodlawn.jpg");
        mp.open();
        Queue<String> routes = new LinkedList<String>(mp.getRoutes());
        mp.setKeyHook(KeyEvent.VK_TAB, onTab, mp, routes);
        mp.breakLeap();
        mp.close();
    }

    private KeyHook onDKey = (keyEvent, args) -> {
        MapCanvas mp = (MapCanvas) args[0];
        int i = (int)args[1];
        mp.setStatusMessage("Key D pressed " + i);
        args[1] = i + 1;
    };
    
    @Test
    public void demoCodeTest() throws IOException {
        MapCanvas mp = new MapCanvas("Woodlawn.jpg");
        mp.open();
        mp.setDemoKeyHooks(true);
        mp.setKeyHook(KeyEvent.VK_D, onDKey, mp, 0);
        mp.breakJump();
        mp.close();
    }

    @Test
    public void moonTest() throws IOException {
        MoonCanvas mc = new MoonCanvas("moon.jpg");
        mc.open();
        mc.setStatusMessage("Moon map opened!");
        mc.breakJump();
        Color[][] areaColors = mc.getArea(0, 0, 50, 50);
        for(int r = 0; r < areaColors.length; r++) {
            for (int c = 0; c < areaColors[r].length; c++) {
                Color orig = areaColors[r][c];
                areaColors[r][c] = new Color(255 - orig.getRed(), 255 - orig.getGreen(), 255-orig.getBlue());
            }
        }
        mc.setStatusMessage("Area fetched and negated");
        mc.breakJump();
        mc.setArea(0, 0, areaColors);
        mc.setStatusMessage("Area updated");
        mc.breakJump();
        mc.close();

    }
}
