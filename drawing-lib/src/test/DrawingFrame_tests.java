import java.awt.image.BufferedImage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
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

    private KeyHook onKeyLeft = (keyEvent, args) -> {
        Drawing drw = (Drawing)args[0];
        DrawingFrame drwFrame = (DrawingFrame)args[1];
        int state = (int)args[2];
        switch(state) {
            case 0:
                drw.restore();
                break;
            case 1:
                drw.restore("negative");
                break;
            case 2:
                drw.restore("grayscale");
                break;
        }
        drwFrame.setStatusMessage("Snapshot %d applied!", state);
        drwFrame.repaint();
        state = (state + 1) % 3;
        args[2] = state;
    };

    @Test
    public void snapshotTest() throws IOException {
        Drawing drw = Drawing.read("src/res/test/test_img1.jpg");
        DrawingFrame drwFrame = new DrawingFrame(drw);
        drwFrame.open();
        drwFrame.breakStep("Frame opened.");
        for (int x = 0; x < drw.getWidth(); x++) {
            for (int y = 0; y < drw.getHeight(); y++) {
                int origRGB = drw.getImage().getRGB(x, y);
                drw.getImage().setRGB(x, y, ~origRGB);
            }
        }
        assertFalse(drw.hasSnapshot("negative"));
        drw.snapshot("negative");
        assertTrue(drw.hasSnapshot("negative"));
        drwFrame.breakStep("Negative snapshot taken!");
        drw.restore();
        for (int x = 0; x < drw.getWidth(); x++) {
            for (int y = 0; y < drw.getHeight(); y++) {
                Color origCol = new Color(drw.getImage().getRGB(x, y));
                int avg = (origCol.getRed() + origCol.getGreen() + origCol.getBlue()) / 3;
                Color newCol = new Color(avg, avg, avg);
                drw.getImage().setRGB(x, y, newCol.getRGB());
            }
        }
        drw.snapshot("grayscale");
        assertTrue(drw.hasSnapshot("grayscale"));
        drwFrame.breakStep("Grayscale snapshot taken");
        drwFrame.setKeyPressedHook(KeyEvent.VK_LEFT, onKeyLeft, drw, drwFrame, 0);
        drwFrame.setStatusMessage("Press left arrow to flip through snapshots!");
        drwFrame.close();
        System.out.println("Snapshot test terminated");
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
        drwFrame.setStatusMessage("Press arrow down while waiting to close!");
        drwFrame.setKeyPressedHook(KeyEvent.VK_DOWN, onKey, drwFrame, 0);
        drwFrame.close();
    }

    @Test
    public void testDebugButtons() throws InterruptedException {
        DrawingFrame frame = new DrawingFrame(new Drawing(240, 100, Color.white));
        frame.open();
        for (int i = 1; i <= 1000; i++) {
            if (i % 100 == 0) {
                frame.breakLeap("Leap %d", i);
            } else {
                frame.breakStep(10, "Step %d w/ 10ms delay", i);
            }
            Thread.sleep(0);
        }
        frame.close();
    }
}
