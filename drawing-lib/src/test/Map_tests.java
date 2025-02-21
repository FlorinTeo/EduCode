import java.awt.event.KeyEvent;
import java.io.IOException;

import org.junit.Test;

import edu.ftdev.KeyInterceptor.KeyHook;
import edu.ftdev.Map.MapFrame;
import edu.ftdev.Map.MapImage;

public class Map_tests {
    private static MapImage _mapImage;
    private static MapFrame _mapFrame;

    private static KeyHook _onKeyT = (KeyEvent keyEvent) -> {
        String statusText = "Key: '" + keyEvent.getKeyChar() + "'; ";
        statusText += "Routes: " + _mapImage.getRoutes();
        _mapFrame.setStatusMessage(statusText);
    };

    @Test
    public void basicTest() throws IOException, InterruptedException {
        // loads an intersection image file and displays it in a map frame.
        _mapImage = MapImage.load("src/test/res/Woodlawn.jpg");
        _mapFrame = new MapFrame(_mapImage);
        
        // registers the key T with the method _onKeyT
        _mapFrame.setKeyTypedHook('T', _onKeyT);
        
        // opens the GUI window
        _mapFrame.open();
        
        // stops, waiting for user action
        _mapFrame.jump();
        
        // close the window and terminate the program
        _mapFrame.close();
    }
}
