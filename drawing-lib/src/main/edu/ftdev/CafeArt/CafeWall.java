package edu.ftdev.CafeArt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFactory;
import edu.ftdev.DrawingFrame;

public class CafeWall extends DrawingFactory {
    private static final int _EDGE = 16;
    private static final Color _BKG_COLOR = new Color(144, 144, 144);
    private static final Color _WALL_COLOR = new Color(128, 128, 128);
    private static final Color _BORDER_DARK_COLOR = new Color(64, 64, 64);
    private static final Color _BORDER_LIGHT_COLOR = new Color(192,192,192);

    private static int _WIDTH = 682;
    private static int _HEIGHT = 432;

    private void rawSetup() {
        Graphics2D g = _drawing.getGraphics();
        g.setStroke(new BasicStroke(1));
        // draw the left and upper (darker) border line
        g.setColor(_BORDER_DARK_COLOR);
        g.drawLine(_EDGE, _EDGE, _EDGE, _drawing.getHeight()-_EDGE);
        g.drawLine(_EDGE, _EDGE, _drawing.getWidth()-_EDGE, _EDGE);
        // draw the right and lower (lighter) border line
        g.setColor(_BORDER_LIGHT_COLOR);
        g.drawLine(_EDGE, _drawing.getHeight()-_EDGE, _drawing.getWidth()-_EDGE, _drawing.getHeight()-_EDGE);
        g.drawLine(_drawing.getWidth()-_EDGE, _EDGE, _drawing.getWidth()-_EDGE, _drawing.getHeight()-_EDGE);
        // fill the wall with the wall color
        g.setColor(_WALL_COLOR);
        g.fillRect(_EDGE+1,_EDGE+1, _drawing.getWidth()-2 * _EDGE-1, _drawing.getHeight()-2 * _EDGE-1);
    }

    public CafeWall() throws IOException {
        try {
            _drawing = new Drawing(_WIDTH, _HEIGHT, _BKG_COLOR);
            _drawingFrame = new DrawingFrame(_drawing);
        } catch (IOException e) {
            // can't happen - resource is in the JAR
            e.printStackTrace();
        }
        rawSetup();
    }

    /**
     * Draws a bright square at the <b>x</b>,<b>y</b> pixel coordinates with a side of <b>size</b> pixels.
     * @param x - x coordinate.
     * @param y - y coordinate.
     * @param size - size of the square side.
     */
    public void drawBrightSquare(int x, int y, int size) {
        Graphics2D g = _drawing.getGraphics();
        int tlX = x + _EDGE;
        int tlY = y + _EDGE;
        g.setColor(Color.WHITE);
        g.fillRect(tlX, tlY, size, size);
        _drawingFrame.repaint();
    }
    
    /**
     * Draws a dark square at the <b>x</b>,<b>y</b> pixel coordinates with a side of <b>size</b> pixels.
     * @param x - x coordinate.
     * @param y - y coordinate.
     * @param size - size of the square side.
     */
    public void drawDarkSquare(int x, int y, int size ) {
        Graphics2D g = _drawing.getGraphics();
        int tlX = x + _EDGE;
        int tlY = y + _EDGE;
        g.setColor(Color.BLACK);
        g.fillRect(tlX, tlY, size, size);
        g.setColor(Color.BLUE);
        g.drawLine(tlX, tlY, tlX + size-1, tlY + size-1);
        g.drawLine(tlX, tlY + size-1, tlX + size-1, tlY);
        _drawingFrame.repaint();
    }
}
