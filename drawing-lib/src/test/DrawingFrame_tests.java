import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFrame;
import edu.ftdev.MouseInterceptor.MouseHook;

public class DrawingFrame_tests {

    public static MouseHook _onMouseClick = (e, args) -> {
        DrawingFrame drwFrame = (DrawingFrame) args[0];
        int x = e.getX();
        int y = e.getY();
        drwFrame.setStatusMessage(String.format("Clicked at (%d, %d)\n", x, y));
    };

    @Test
    public void basicTest() throws IOException, InterruptedException {
        System.out.println("DrawingLib testing code!");
        File drwFile = new File("src/res/test/test_img1.jpg");
        BufferedImage drwImg = ImageIO.read(drwFile);
        Drawing drw = new Drawing(drwImg);
        DrawingFrame drwFrame = new DrawingFrame(drw);
        drwFrame.open();
        System.out.println("Sleeping 10sec");
        Thread.sleep(10000);
        System.out.println("Pausing");
        drwFrame.breakStep();
        for(int i = 0; i < 10; i++) {
            System.out.println(i);
            drwFrame.breakStep(1000);
        }
        System.out.println("Stopping");
        drwFrame.breakStep();
        System.out.println("Leaping");
        drwFrame.breakLeap();
        System.out.println("MouseHook enabled for 10sec!");
        // enabling the mouse hook. While mouse hook enabled, step() and leap() methods are inactive (pass-through)!
        drwFrame.setMouseClickedHook(_onMouseClick, drwFrame);
        drwFrame.breakLeap();
        Thread.sleep(10000);

        // disable the mouse hook. When disabled, stop() and step() are active!
        drwFrame.setMouseClickedHook(null);
        System.out.println("MouseHook disabled!");
        drwFrame.breakLeap();

        // close the frame
        drwFrame.close();
        System.out.println("Goodbye!");
    }

    @Test
    public void doubleFrameTest() throws IOException {
        File drwFile = new File("src/res/test/test_img1.jpg");
        BufferedImage drwImg = ImageIO.read(drwFile);
        Drawing drw1 = new Drawing(drwImg);
        DrawingFrame drwFrame1 = new DrawingFrame(drw1);
        drwFrame1.open();
        Drawing drw2 = new Drawing(400, 300, Color.cyan);
        DrawingFrame drwFrame2 = new DrawingFrame(drw2);
        drwFrame2.open();
        drwFrame1.breakLeap();
        drwFrame1.close();
        drwFrame2.breakLeap();
        drwFrame2.close();
    }
}
