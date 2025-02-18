import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.io.IOException;

import org.junit.Test;

import edu.ftdev.Maze.MazeCanvas;
import edu.ftdev.Maze.MazeCanvas.Side;

public class MazeCanvas_tests {
    @Test
    public void basicTest() throws IOException, InterruptedException {
        MazeCanvas mazeCanvas = new MazeCanvas(16,24,32);
        assertEquals(16, mazeCanvas.getRows());
        assertEquals(24, mazeCanvas.getCols());
        mazeCanvas.open();
        mazeCanvas.step();
        mazeCanvas.drawCell(1, 1);
        mazeCanvas.step();
        mazeCanvas.drawCell(1, 2);
        mazeCanvas.drawCell(2, 1);
        mazeCanvas.drawCell(2, 2);
        mazeCanvas.step();
        mazeCanvas.drawShade(2, 2, Color.GRAY.brighter());
        mazeCanvas.step();
        mazeCanvas.eraseWall(1, 1, Side.Right);
        mazeCanvas.eraseWall(1, 2, Side.Left);
        mazeCanvas.step();
        mazeCanvas.eraseWall(1, 1, Side.Bottom);
        mazeCanvas.eraseWall(2, 1, Side.Top);
        mazeCanvas.leap();
        mazeCanvas.close();
    }

    @Test
    public void snakeTest() throws IOException, InterruptedException {
        MazeCanvas mazeCanvas = new MazeCanvas(32, 48, 16);
        mazeCanvas.open();
        mazeCanvas.leap();
        for(int r = 0; r < mazeCanvas.getRows(); r++) {
            if (r == 1 || r == (mazeCanvas.getRows() - 1)) {
                mazeCanvas.step();
            }
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
                }
            }
        }
        mazeCanvas.leap();
        mazeCanvas.close();
    }
}
