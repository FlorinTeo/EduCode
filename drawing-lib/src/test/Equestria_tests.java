import java.io.IOException;

import org.junit.Test;

import edu.ftdev.Equestria.EquestriaMap;

public class Equestria_tests {
    @Test
    public void demoTest() throws IOException, InterruptedException {
        EquestriaMap map = EquestriaMap.create();
		// open the map of Equestria window then wait for the key 'S' (step) to be pressed.
		map.open();
		map.step();
		
		// plot a white dot on the map at coordinates (10, 5)
		map.plot(6, 12);
		map.step();

		// draw a white line on the map from the last point (10, 5) to the point (30, 2)
		map.lineTo(30, 2);
		map.step();

		// draw a line from point (19, 11) to point (19, 20)
		// then wait for any of the keys 'S' (step), 'C' (continue) or 'Q' (quit) to be pressed.
		map.line(19, 11, 19,  20);
		map.step();

		// draw a circle with center in (19, 11) and diameter 4
		map.circle(19, 11, 4);
		map.leap();
		
		// close the map of Equestria window.
		map.close();
    }
    
}
