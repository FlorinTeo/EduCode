import static org.junit.Assert.assertEquals;

import java.awt.Color;

import org.junit.Test;

import edu.ftdev.Maze.MazeCanvas;
import edu.ftdev.Maze.MazeCanvas.Side;

public class MazeCanvas_tests {
    @Test
    public void basicTest() {
        MazeCanvas mazeCanvas = new MazeCanvas(16,24,32);
        assertEquals(16, mazeCanvas.getRows());
        assertEquals(24, mazeCanvas.getCols());
        mazeCanvas.open();
        mazeCanvas.breakStep();
        mazeCanvas.drawCell(1, 1);
        mazeCanvas.breakStep();
        mazeCanvas.drawCell(1, 2);
        mazeCanvas.drawCell(2, 1);
        mazeCanvas.drawCell(2, 2);
        mazeCanvas.breakStep();
        mazeCanvas.drawShade(2, 2, Color.GRAY.brighter());
        mazeCanvas.breakStep();
        mazeCanvas.eraseWall(1, 1, Side.Right);
        mazeCanvas.eraseWall(1, 2, Side.Left);
        mazeCanvas.breakStep();
        mazeCanvas.eraseWall(1, 1, Side.Bottom);
        mazeCanvas.eraseWall(2, 1, Side.Top);
        mazeCanvas.breakLeap();
        mazeCanvas.close();
    }

    @Test
    public void pathsTest() {
        MazeCanvas mc = new MazeCanvas();
        mc.open();
        for (int i = 0; i < 16; i++) {
            int r = i / 4;
            int c = i % 4;
            mc.drawCell(r, c);
            if (i == 0 || i == 6 || i == 11) {
                mc.eraseWall(r, c, Side.Left);
                mc.drawPath(r, c, Side.Left, Color.RED);
                mc.drawPath(r, c, Side.Center, Color.RED);
                mc.drawPath(r, c, Side.Bottom, Color.RED);
                mc.eraseWall(r, c, Side.Bottom);
                mc.breakStep();
            } else if (i == 4 || i == 10 || i == 15) {
                mc.eraseWall(r, c, Side.Top);
                mc.drawPath(r, c, Side.Top, Color.RED);
                mc.drawPath(r, c, Side.Center, Color.RED);
                mc.drawPath(r, c, Side.Right, Color.RED);
                mc.eraseWall(r, c, Side.Right);
                mc.breakStep();
            } else if (i == 5) {
                mc.eraseWall(r, c, Side.Left);
                mc.drawPath(r, c, Side.Left, Color.RED);
                mc.drawPath(r, c, Side.Center, Color.RED);
                mc.drawPath(r, c, Side.Right, Color.RED);
                mc.eraseWall(r, c, Side.Right);
                mc.breakStep();
            }
        }
        mc.drawShade(0, 0, Color.CYAN.brighter());
        mc.drawShade(2, 1, Color.LIGHT_GRAY);
        mc.drawPath(1, 2, Side.Center, Color.RED.darker());
        mc.breakJump();
        mc.close();
    }

    @Test
    public void docCodeTest() {
        MazeCanvas mc = new MazeCanvas(4, 10, 32);
        // open the maze canvas
        mc.open();
        for (int r = 0; r < mc.getRows(); r++) {
            for (int c = 0; c < mc.getCols(); c++) {
                mc.drawCell(r, c);
            }
        }
        // customize cell at coordinates (1, 1)
        mc.eraseWall(1, 1, Side.Right);
        mc.drawPath(1, 1, Side.Center, Color.RED.darker());
        mc.drawPath(1, 1, Side.Right, Color.RED);
        // customize cell at coordinates (1, 2)
        mc.eraseWall(1, 2, Side.Left);
        mc.drawPath(1, 2, Side.Left, Color.RED);
        mc.drawPath(1, 2, Side.Center, Color.RED.darker());
        // customize cell at coordinates (2, 1)
        mc.drawShade(2, 2, Color.GREEN.brighter());
        // suspend execution until action then terminate
        mc.breakStep();
        mc.close();
    }

    @Test
    public void snakeTest() {
        MazeCanvas mazeCanvas = new MazeCanvas(32, 48, 16);
        mazeCanvas.open();
        for(int r = 0; r < mazeCanvas.getRows(); r++) {
            for(int c = 0; c < mazeCanvas.getCols(); c++) {
                mazeCanvas.drawCell(r, c);
                if (r == 0) {
                    mazeCanvas.eraseWall(r, c, Side.Bottom);
                    mazeCanvas.drawPath(r, c, Side.Center, Color.RED.darker());
                    mazeCanvas.drawPath(r, c, Side.Bottom, Color.RED);
                    if (c % 2 == 0) {
                        mazeCanvas.eraseWall(r, c, Side.Left);
                        mazeCanvas.drawPath(r, c, Side.Left, Color.RED);
                    } else {
                        mazeCanvas.eraseWall(r, c, Side.Right);
                        mazeCanvas.drawPath(r, c, Side.Right, Color.RED);
                    }
                } else if (r == (mazeCanvas.getRows() - 1)) {
                    mazeCanvas.eraseWall(r, c, Side.Top);
                    mazeCanvas.drawPath(r, c, Side.Top, Color.RED);
                    mazeCanvas.drawPath(r, c, Side.Center, Color.RED.darker());
                    if (c % 2 == 0) {
                        mazeCanvas.eraseWall(r, c, Side.Right);
                        mazeCanvas.drawPath(r, c, Side.Right, Color.RED);
                    } else {
                        mazeCanvas.eraseWall(r, c, Side.Left);
                        mazeCanvas.drawPath(r, c, Side.Left, Color.RED);
                    }
                } else {
                    mazeCanvas.eraseWall(r, c, Side.Top);
                    mazeCanvas.eraseWall(r, c, Side.Bottom);
                    mazeCanvas.drawPath(r, c, Side.Top, Color.RED);
                    mazeCanvas.drawPath(r, c, Side.Bottom, Color.RED);
                    mazeCanvas.drawPath(r, c, Side.Center, Color.RED);
                    // when drawing middle cells, step at every step, or delay if leaping
                    mazeCanvas.breakStep(10);
                }
            }
            if (r == 0 || r == (mazeCanvas.getRows() - 1)) {
                mazeCanvas.breakLeap();
            }
        }
        mazeCanvas.breakJump();
        mazeCanvas.close();
    }
}
