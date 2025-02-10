import java.io.IOException;

import org.junit.Test;

import edu.ftdev.DrawingFrame;
import edu.ftdev.CafeArt.CafeWall;

public class CafeWall_tests {

    @Test
    public void ctorTest() throws IOException, InterruptedException {
        CafeWall cafeWall = new CafeWall();
        DrawingFrame drwFrame = new DrawingFrame(cafeWall);
        drwFrame.open();
        drwFrame.stop();
        drwFrame.close();
    }

    @Test
    public void drawSquaresTest() throws IOException, InterruptedException {
        CafeWall cafeWall = new CafeWall();
        DrawingFrame drwFrame = new DrawingFrame(cafeWall);
        drwFrame.open();
        drwFrame.step();
        cafeWall.drawBrightSquare(75, 60, 80);
        drwFrame.step();
        cafeWall.drawDarkSquare(180, 95, 55);
        drwFrame.stop();
        drwFrame.close();
    }
}
