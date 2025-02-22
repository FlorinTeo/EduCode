import java.awt.event.KeyEvent;
import java.io.IOException;

import org.junit.Test;

import edu.ftdev.KeyInterceptor.KeyHook;
import edu.ftdev.Map.MapCanvas;

public class Map_tests {
    private static MapCanvas _mapCanvas;

    private static KeyHook _onKeyT = (KeyEvent keyEvent) -> {
        String statusText = "Key: '" + keyEvent.getKeyChar() + "'; ";
        statusText += "Routes: " + _mapCanvas.getRoutes();
        _mapCanvas.setStatusMessage(statusText);
    };

    @Test
    public void routesTest() throws IOException, InterruptedException {
        // loads an intersection image file and displays it in a map frame.
        _mapCanvas = new MapCanvas("Ravenna.jpg");
        // registers the key T with the method _onKeyT
        _mapCanvas.setKeyHook('T', _onKeyT);
        // opens the GUI window
        _mapCanvas.open();
        _mapCanvas.setStatusMessage("Press T to display the routes");
        // stops, waiting for user action
        _mapCanvas.jump();
        // close the window and terminate the program
        _mapCanvas.close();
    }

    @Test
    public void collisionTest() throws IOException, InterruptedException {
        // opens the GUI window
        _mapCanvas = new MapCanvas("Woodlawn.jpg");
        _mapCanvas.open();
        _mapCanvas.step();
        // sets the overlays to the routes "AB" and "CD" and checks if they collide. They should not.
        _mapCanvas.setOverlays("BA", "CD");
        _mapCanvas.setStatusMessage(_mapCanvas.collide("BA", "CD") ? "Collide" : "Not collide");
        _mapCanvas.step();
        // sets the overlays to the routes "AD" and "CE" and checks if they collide. They should.
        _mapCanvas.setOverlays("AD", "CE");
        _mapCanvas.setStatusMessage(_mapCanvas.collide("AD", "CE") ? "Collide" : "Not collide");
        _mapCanvas.jump();
        _mapCanvas.close();
    }

    @Test
    public void demoKeyHooksTest() throws IOException, InterruptedException {
        _mapCanvas = new MapCanvas("Loyal.jpg");
        _mapCanvas.setOverlays();
        _mapCanvas.open();
        _mapCanvas.setStatusMessage("demo key hooks enabled");
        _mapCanvas.setDemoKeyHooks(true);
        _mapCanvas.jump();
        _mapCanvas.setStatusMessage("demo key hooks disabled");
        _mapCanvas.setDemoKeyHooks(false);
        _mapCanvas.jump();
        _mapCanvas.close();
    }
}
