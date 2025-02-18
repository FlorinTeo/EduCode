import java.io.IOException;

import org.junit.Test;

import edu.ftdev.CafeArt.CafeWall;

public class CafeWall_tests {

    @Test
    public void ctorTest() throws IOException, InterruptedException {
        CafeWall cafeWall = new CafeWall();
        cafeWall.open();
        cafeWall.leap();
        cafeWall.close();
    }

    @Test
    public void drawSquaresTest() throws IOException, InterruptedException {
        CafeWall cafeWall = new CafeWall();
        cafeWall.open();
        cafeWall.step();
        cafeWall.drawBrightSquare(75, 60, 80);
        cafeWall.step();
        cafeWall.drawDarkSquare(180, 95, 55);
        cafeWall.leap();
        cafeWall.close();
    }
}
