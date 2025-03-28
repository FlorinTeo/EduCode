import org.junit.Test;

import edu.ftdev.Equestria.EquestriaMap;

public class Equestria_tests {
    @Test
    public void demoTest() {
		// create a new map of Equestria
        EquestriaMap map = new EquestriaMap();

		// open the map of Equestria window then wait for the key 'S' (step) to be pressed.
		map.open();
		map.breakLeap();
		
		// plot a white dot on the map at coordinates (10, 5)
		map.plot(6, 12);
		map.breakStep();

		// draw a white line on the map from the last point (10, 5) to the point (30, 2)
		map.lineTo(30, 2);
		map.breakStep();

		// draw a line from point (19, 11) to point (19, 20)
		// then wait for any of the keys 'S' (step), 'C' (continue) or 'Q' (quit) to be pressed.
		map.line(19, 11, 19,  20);
		map.breakStep();

		// draw a circle with center in (19, 11) and diameter 4
		map.circle(19, 11, 4);
		map.breakJump();

		// clear the map of Equestria
		map.clear();
		map.breakLeap();

		// draw two concentric circles
		map.circle(10, 10, 2);
		map.circle(10, 10, 4);
		map.breakJump();
		
		// close the map of Equestria window.
		map.close();
    }
    
}
