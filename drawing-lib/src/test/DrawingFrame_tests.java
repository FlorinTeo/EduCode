import java.awt.image.BufferedImage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFrame;
import edu.ftdev.KeyInterceptor.KeyHook;
import edu.ftdev.MouseInterceptor.MouseHook;

public class DrawingFrame_tests {

    private static MouseHook _onMouseClick = (e, args) -> {
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

    @Test
    public void snapshotTest() throws IOException {
        Drawing drw = Drawing.read("src/res/test/test_img1.jpg");
        DrawingFrame drwFrame = new DrawingFrame(drw);
        drwFrame.open();
        drwFrame.setStatusMessage("Frame opened.");
        drwFrame.breakStep();
        Graphics2D g = drw.getGraphics();
        g.setColor(Color.BLACK);
        g.drawLine(0, 0, drw.getWidth(), drw.getHeight());
        drwFrame.setStatusMessage("Diagonal down drawn.");
        drwFrame.breakStep();
        drw.restore();
        drwFrame.setStatusMessage("Image restored to default snapshot.");
        drwFrame.breakStep();
        g.drawLine(0, drw.getHeight(), drw.getWidth(), 0);
        drw.snapshot();
        g.drawLine(0, 0, drw.getWidth(), drw.getHeight());
        drwFrame.setStatusMessage("Diagonal up > snapshot > Diagonal down.");
        drwFrame.breakStep();
        drw.restore();
        drwFrame.setStatusMessage("Restore to default snapshot (diagonal up).");
        drwFrame.breakJump();
        drwFrame.close();
    }

    private static KeyHook _onXTyped = (keyEvent, args) -> {
        DrawingFrame drwFrame = (DrawingFrame)args[0];
        int iteration = (int)args[1];
        drwFrame.setStatusMessage(String.format("[%d] intercept on key '%c'", iteration, keyEvent.getKeyChar()));
        drwFrame.breakStep(String.format("[%d] break on key '%c'", iteration, keyEvent.getKeyChar()));
        args[1] = iteration+1;
    };

    @Test
    public void keyInterceptorTest() throws IOException, InterruptedException {
        Drawing drw = Drawing.read("src/res/test/test_img1.jpg");
        DrawingFrame drwFrame = new DrawingFrame(drw);
        drwFrame.open();
        System.out.println("breakLeap.");
        drwFrame.breakLeap();
        System.out.println("setKeyTyepdHook('X' -> _onXTyped).");
        drwFrame.setKeyTypedHook('X', _onXTyped, drwFrame, 0);
        System.out.println("breakLeap before sleep");
        drwFrame.breakLeap();
        System.out.println("sleep 10sec");
        Thread.sleep(10000);
        System.out.println("breakJump before setKeyTypedHook('X' -> null)");
        drwFrame.breakLeap();
        System.out.println("setKeyTypedHook('X' -> null)");
        drwFrame.setKeyTypedHook('X', null);
        System.out.println("breakJump before closing");
        drwFrame.breakJump("Closing the window!");
        System.out.println("close()");
        drwFrame.close();
    }

    @Test
    public void breakReturnsTest() throws IOException {
        Drawing drw = Drawing.read("src/res/test/test_img1.jpg");
        DrawingFrame drwFrame = new DrawingFrame(drw);
        assertFalse(drwFrame.breakJump("Not breaking due to frame not being opened"));
        drwFrame.open();
        assertTrue(drwFrame.breakStep("Breaking in step!"));
        drwFrame.setKeyTypedHook('X', _onXTyped);
        assertFalse(drwFrame.breakStep("Not breaking due to custom key hook!"));
        drwFrame.setKeyTypedHook('X', null);
        assertTrue(drwFrame.breakLeap("Breaking due to custom key hook removed!!"));
        drwFrame.close();
        assertFalse(drwFrame.breakJump("Not breaking due to frame being closed!"));
        System.out.println("Program terminated!");
    }

    private KeyHook onKey = (keyEvent, args) -> {
        DrawingFrame frame = (DrawingFrame)args[0];
        int number = (int)args[1];
        number++;
        frame.setStatusMessage(String.format("Key pressed %d times", number));
        args[1] = number;
    };

    @Test
    public void blockingCloseTest() throws IOException {
        Drawing drw = Drawing.read("src/res/test/test_img1.jpg");
        DrawingFrame drwFrame = new DrawingFrame(drw);
        drwFrame.open();
        drwFrame.setKeyPressedHook(KeyEvent.VK_DOWN, onKey, drwFrame, 0);
        drwFrame.close();
    }
}
