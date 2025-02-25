import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ftdev.CafeArt.CafeWall;

public class CafeWall_tests {

    @Test
    public void ctorTest() throws InterruptedException {
        CafeWall cafeWall = new CafeWall();
        assertEquals(650, cafeWall.getWidth());
        assertEquals(400, cafeWall.getHeight());
        cafeWall.open();
        cafeWall.breakLeap();
        cafeWall.close();
    }

    @Test
    public void drawSquaresTest() throws InterruptedException {
        CafeWall cafeWall = new CafeWall();
        cafeWall.open();
        cafeWall.breakStep();
        cafeWall.drawBrightSquare(75, 60, 80);
        cafeWall.breakStep();
        cafeWall.drawDarkSquare(180, 95, 55);
        cafeWall.breakLeap();
        cafeWall.close();
    }
}
