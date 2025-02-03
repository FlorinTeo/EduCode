import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFrame;
import edu.ftdev.MouseInterceptor.MouseHook;

public class Program {

    public static MouseHook _onMouseClick = (MouseEvent e) -> {
        int x = e.getX();
        int y = e.getY();
        System.out.printf("Clicked at (%d, %d)\n", x, y);
    };
    
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("DrawingLib testing code!");
        File drwFile = new File("src/test.jpg");
        BufferedImage drwImg = ImageIO.read(drwFile);
        Drawing drw = new Drawing(drwImg);
        DrawingFrame drwFrame = new DrawingFrame(drw);
        drwFrame.open();
        drwFrame.setMouseClickedHook(_onMouseClick);
        System.out.println("Pausing");
        drwFrame.step();
        for(int i = 0; i < 10; i++) {
            System.out.println(i);
            drwFrame.step(1000);
        }
        System.out.println("Stopping & removing hook");
        drwFrame.setMouseClickedHook(null);
        drwFrame.stop();
        drwFrame.close();
        System.out.println("Goodbye!");
    }
}